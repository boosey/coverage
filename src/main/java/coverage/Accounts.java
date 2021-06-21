package coverage;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.bson.types.ObjectId;
import io.smallrye.mutiny.Uni;

@Path("/accounts")
public class Accounts {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<Account>> list() {
        return Account.listAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> addAccount(Account a) {

      return a.persist()
        .onItem()
          .transform(v -> Response.ok().build())
        .onFailure()
          .recoverWithItem(Response.status(Status.NOT_ACCEPTABLE).build());

    }

    @DELETE
    public Uni<Response> deleteAllAccounts() {

      return Account.deleteAll()
              .onItem()
                .transform(count -> Response.ok().entity(count).build())
              .onFailure()
                .recoverWithItem(Response.status(Status.INTERNAL_SERVER_ERROR).build());
    }

    @DELETE
    @Path("/{accountId}")
    public Uni<Response> deleteAccountById(@PathParam("accountId") String id) {

      return Account.deleteById(new ObjectId(id))
        .onItem()
          .transform(succeeded -> {
            if (succeeded) {
              return Response.ok().build();
            } else {
              return Response.status(Status.NOT_FOUND).build();
            }
          })
          .onFailure()
            .recoverWithItem(Response.status(Status.INTERNAL_SERVER_ERROR).build());
    }
}