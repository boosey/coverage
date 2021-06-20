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
        return Uni.createFrom().item(Account.listAll());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> addAccount(Account a) {

      try {
        a.persist();
        return Uni.createFrom().item(Response.ok().build());
      } catch (Exception e) {
        return Uni.createFrom().item(Response.status(Status.NOT_ACCEPTABLE).build());
      }
    }

    @DELETE
    public Uni<Response> deleteAllAccounts() {

      try {
        return Uni.createFrom().item(Response.ok().entity(Account.deleteAll()).build());
      } catch (Exception e) {
        return Uni.createFrom().item(Response.status(Status.INTERNAL_SERVER_ERROR).build());
      }
    }

    @DELETE
    @Path("/{accountId}")
    public Uni<Response> deleteAccountById(@PathParam("accountId") String id) {

      
      if (Account.deleteById(new ObjectId(id))) {
        return Uni.createFrom().item(Response.ok().build());
      } else {
        return Uni.createFrom().item(Response.status(Status.NOT_FOUND).build());
      }

    }
}