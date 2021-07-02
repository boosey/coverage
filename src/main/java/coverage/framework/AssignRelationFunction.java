package coverage.framework;

import java.util.Optional;

public interface AssignRelationFunction {
  public void relation(Optional<? extends EntitySuper> parent, String childId);
}
