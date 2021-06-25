package coverage;

import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.bson.types.ObjectId;

public interface ServiceInterface {
  public Uni<Response> list();

  public Uni<Response> findById(String id);

  public Uni<Response> add(Account account, UriInfo uriInfo);

  public Uni<Response> delete();

  public Uni<Response> deleteById(String id);

  public Uni<Response> update(String id, Account updates);

  public <T extends EntitySuper> Uni<List<T>> listUni();

  public <T extends EntitySuper> Uni<Optional<T>> findByIdOptionalUni(
    ObjectId id
  );

  public Uni<Long> deleteAllUni();

  public Uni<Boolean> deleteByIdUni(ObjectId id);
}
