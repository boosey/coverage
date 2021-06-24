package coverage;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public interface ServiceInterface {
  public Uni<Response> list();

  public Uni<Response> findById(String id);

  public Uni<Response> add(
    ReactivePanacheMongoEntity item,
    @Context UriInfo uriInfo
  );

  public Uni<Response> delete();

  public Uni<Response> deleteById(String id);

  public Uni<Response> update(String id, Account updates);
}
