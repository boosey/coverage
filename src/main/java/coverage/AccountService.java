package coverage;

import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
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
public class AccountService implements ServiceInterface {

  @Inject
  ServiceDelegate delegate;

  @Override
  public <T extends EntitySuper> Uni<List<T>> listUni() {
    return Account.listAll();
  }

  @Override
  public <T extends EntitySuper> Uni<Optional<T>> findByIdOptionalUni(
    ObjectId id
  ) {
    return Account.findByIdOptional(id);
  }

  @Override
  public Uni<Long> deleteAllUni() {
    return Account.deleteAll();
  }

  @Override
  public Uni<Boolean> deleteByIdUni(ObjectId id) {
    return Account.deleteById(id);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> list() {
    return delegate.list(this);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{id}")
  public Uni<Response> findById(@PathParam("id") String id) {
    return delegate.findById(this, id);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<Response> add(Account account, @Context UriInfo uriInfo) {
    return delegate.add(this, account, uriInfo);
  }

  @DELETE
  public Uni<Response> delete() {
    return delegate.delete(this);
  }

  @DELETE
  @Path("/{id}")
  public Uni<Response> deleteById(@PathParam("id") String id) {
    return delegate.deleteById(this, id);
  }

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<Response> update(@PathParam("id") String id, Account updates) {
    return delegate.update(this, id, updates);
  }
}
