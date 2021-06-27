package coverage;

import coverage.framework.ServiceInterface;
import coverage.framework.ServiceSuper;
import io.smallrye.mutiny.Uni;
import javax.ws.rs.Consumes;
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
  @Path("/{accountId}/assignTalent2/{talentId}")
  public Uni<Response> assignTalent2(
    @PathParam("accountId") String accountId,
    @PathParam("talentId") String talentId
  ) {
    return Talent
      .findByIdOptional(new ObjectId(talentId))
      .onItem()
      .transformToUni(
        talentOptional -> {
          if (talentOptional.isPresent()) {
            return Account
              .findByIdOptional(new ObjectId(accountId))
              .onItem()
              .transformToUni(
                accountOptional -> {
                  if (accountOptional.isPresent()) {
                    Account account = (Account) accountOptional.get();
                    Talent talent = (Talent) talentOptional.get();
                    account.assignedTalent.add(talent);
                    return account
                      .update()
                      .onItem()
                      .<Response>transform(v -> Response.ok().build());
                  } else {
                    return Uni
                      .createFrom()
                      .item(
                        Response
                          .status(Status.NOT_FOUND)
                          .entity("Account not found")
                          .build()
                      );
                  }
                }
              );
          } else {
            return Uni
              .createFrom()
              .item(
                Response
                  .status(Status.NOT_FOUND)
                  .entity("Talent not found")
                  .build()
              );
          }
        }
      )
      .onFailure()
      .recoverWithItem(Response.status(Status.INTERNAL_SERVER_ERROR).build());
  }

  @POST
  @Path("/{accountId}/assignTalent/{talentId}")
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
      .<Response>transformToUni(
        tuple -> {
          if (tuple.getItem1().isPresent() && tuple.getItem2().isPresent()) {
            Account account = tuple.getItem1().get();
            Talent talent = tuple.getItem2().get();
            account.assignTalent(talent);

            return account
              .update()
              .onItem()
              .transform(v1 -> Response.ok().build())
              .onFailure()
              .recoverWithItem(
                error ->
                  Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .build()
              );
          } else {
            return Uni
              .createFrom()
              .item(Response.status(Status.NOT_FOUND).build());
          }
        }
      )
      .onFailure()
      .recoverWithItem(
        error ->
          Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build()
      );
  }
}
