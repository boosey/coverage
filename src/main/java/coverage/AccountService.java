package coverage;

import io.smallrye.mutiny.Uni;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.bson.types.ObjectId;

@Path("/accounts")
public class AccountService {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> list() {
    return Account
      .listAll()
      .onItem()
      .transform(accts -> Response.ok().entity(accts).build())
      .onFailure()
      .recoverWithItem(
        err -> Response.status(Status.INTERNAL_SERVER_ERROR).entity(err).build()
      );
  }

  @GET
  @Path("/{accountId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> getAccount(@PathParam("accountId") String id) {
    return Account
      .findByIdOptional(new ObjectId(id))
      .onItem()
      .transform(
        a -> {
          if (a.isPresent()) {
            return Response.ok().entity(a).build();
          } else {
            return Response.status(Status.NOT_FOUND).entity(a).build();
          }
        }
      )
      .onFailure()
      .recoverWithItem(
        err -> Response.status(Status.INTERNAL_SERVER_ERROR).entity(err).build()
      );
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<Response> addAccount(Account a) {
    // Need to validate account

    return a
      .persist()
      .onItem()
      .transform(v -> Response.ok().build())
      .onFailure()
      .recoverWithItem(
        err -> Response.status(Status.INTERNAL_SERVER_ERROR).entity(err).build()
      );
  }

  @PUT
  @Path("/{accountId}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<Response> updateAccount(
    @PathParam("accountId") String id,
    Account a
  ) {
    return Account
      .<Account>findByIdOptional(new ObjectId(id))
      .onItem()
      .transformToUni(
        ao -> {
          if (ao.isPresent()) {
            Account a1 = ao.get();

            a1.name = a.name;
            a1.address = a.address;
            a1.city = a.city;
            a1.zip = a.zip;

            return a1.update().onItem().transform(v -> Response.ok().build());
          } else {
            return Uni
              .createFrom()
              .item(Response.status(Status.NOT_FOUND).entity(a).build());
          }
        }
      )
      .onFailure()
      .recoverWithItem(
        err -> Response.status(Status.INTERNAL_SERVER_ERROR).entity(err).build()
      );
  }

  @DELETE
  public Uni<Response> deleteAllAccounts() {
    return Account
      .deleteAll()
      .onItem()
      .transform(count -> Response.ok().entity(count).build())
      .onFailure()
      .recoverWithItem(Response.status(Status.INTERNAL_SERVER_ERROR).build());
  }

  @DELETE
  @Path("/{accountId}")
  public Uni<Response> deleteAccountById(@PathParam("accountId") String id) {
    return Account
      .deleteById(new ObjectId(id))
      .onItem()
      .transform(
        succeeded -> {
          if (succeeded) {
            return Response.ok().build();
          } else {
            return Response.status(Status.NOT_FOUND).build();
          }
        }
      )
      .onFailure()
      .recoverWithItem(Response.status(Status.INTERNAL_SERVER_ERROR).build());
  }
}
