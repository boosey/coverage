package coverage;

import javax.inject.Inject;
import org.jboss.logging.Logger;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;

public class Account extends ReactivePanacheMongoEntity {
  public String name;
  public String address;
  public String city;
  public String state;
  public String zip;

  @Inject
   static Logger log;

}
