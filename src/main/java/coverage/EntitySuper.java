package coverage;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;

public class EntitySuper
  extends ReactivePanacheMongoEntity
  implements EntityInterface {

  // public <T extends EntitySuper> void updateFields(T updates) {
  //   throw new UnsupportedOperationException(
  //     "A subclass must implement this method"
  //   );
  // }

  @Override
  public <T extends EntityInterface> void updateFields(T updates) {
    throw new UnsupportedOperationException(
      "A subclass must implement this method"
    );
  }
}
