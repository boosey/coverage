package coverage.framework;

import io.smallrye.mutiny.Uni;
import java.util.Optional;
import org.bson.types.ObjectId;

public interface FindByIdOptionalUniFunction {
  public <E extends EntitySuper> Uni<Optional<E>> apply(ObjectId id);
}
