package coverage;

public interface EntityInterface {
  public <T extends EntityInterface> void updateFields(T updates);
}
