package coverage;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

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
    public Uni<String> addAccount(Account a) {

      a.persist();
      return Uni.createFrom().item("OK");

    }
}