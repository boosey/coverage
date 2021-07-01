package coverage;

import coverage.framework.ServiceInterface;
import coverage.framework.ServiceSuper;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.bson.types.ObjectId;

@Path("/accounts")
public class AccountService extends ServiceSuper implements ServiceInterface {

  AccountService() {
    super(
      () -> Account.listAll(),
      id -> Account.findByIdOptional(id),
      () -> Account.deleteAll(),
      id -> Account.deleteById(id)
    );
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<Response> add(Account a, @Context UriInfo uriInfo) {
    return this.addEntity(a, uriInfo);
  }

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<Response> update(String id, Account updates) {
    return this.updateEntity(id, updates);
  }

  @POST
  @Path("/{accountId}/squadManager/{talentId}")
  public Uni<Response> assignTalent(
    @PathParam("accountId") String accountId,
    @PathParam("talentId") String talentId
  ) {
    return Uni
      .combine()
      .all()
      .unis(
        Account.<Account>findByIdOptional(new ObjectId(accountId)),
        Talent.<Talent>findByIdOptional(new ObjectId(talentId))
      )
      .asTuple()
      .onItem()
      .transform(
        tuple -> {
          if (tuple.getItem1().isPresent() && tuple.getItem2().isPresent()) {
            return Tuple2.of(
              tuple.getItem1().get(),
              tuple.getItem2().get().id.toString()
            );
          } else {
            throw new NotFoundException();
          }
        }
      )
      .onItem()
      .transformToUni(
        tuple -> {
          Account a = tuple.getItem1();
          a.squadManagerId = tuple.getItem2();
          return a.update();
        }
      )
      .onItem()
      .transform(v -> Response.ok().build())
      .onFailure(error -> error.getClass() == NotFoundException.class)
      .recoverWithItem(Response.status(Status.NOT_FOUND).build())
      .onFailure()
      .recoverWithItem(
        err -> Response.status(Status.INTERNAL_SERVER_ERROR).entity(err).build()
      );
  }
}
