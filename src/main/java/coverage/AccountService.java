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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.bson.types.ObjectId;

@Path("/accounts")
public class AccountService extends ServiceSuper implements ServiceInterface {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> list() {
    return super.list(Account.listAll());
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{id}")
  public Uni<Response> findById(@PathParam("id") String id) {
    return super.findById(Account.findByIdOptional(new ObjectId(id)));
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<Response> add(Account account, @Context UriInfo uriInfo) {
    return super.add(account, uriInfo);
  }

  @DELETE
  public Uni<Response> delete() {
    return super.delete(Account.deleteAll());
  }

  @DELETE
  @Path("/{id}")
  public Uni<Response> deleteById(@PathParam("id") String id) {
    return super.deleteById(Account.deleteById(new ObjectId(id)));
  }

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<Response> update(@PathParam("id") String id, Account updates) {
    return super.update(Account.findByIdOptional(new ObjectId(id)), updates);
  }
}
