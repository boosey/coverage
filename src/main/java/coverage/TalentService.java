package coverage;

import coverage.framework.EntitySuper;
import coverage.framework.ServiceDelegate;
import coverage.framework.ServiceInterface;
import coverage.framework.ServiceSuper;
import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.bson.types.ObjectId;

@Path("/talent")
public class TalentService extends ServiceSuper implements ServiceInterface {

  @Inject
  ServiceDelegate delegate;

  @Override
  public <T extends EntitySuper> Uni<List<T>> listUni() {
    return Talent.listAll();
  }

  @Override
  public <T extends EntitySuper> Uni<Optional<T>> findByIdOptionalUni(
    ObjectId id
  ) {
    return Talent.findByIdOptional(id);
  }

  @Override
  public Uni<Long> deleteAllUni() {
    return Talent.deleteAll();
  }

  @Override
  public Uni<Boolean> deleteByIdUni(ObjectId id) {
    return Talent.deleteById(id);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<Response> add(Talent a, @Context UriInfo uriInfo) {
    return this.addEntity(a, uriInfo);
  }

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<Response> update(String id, Talent updates) {
    return this.updateEntity(id, updates);
  }
}
