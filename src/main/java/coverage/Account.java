package coverage;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;

public class Account extends ReactivePanacheMongoEntity {

  public String name;
  public String address;
  public String city;
  public String state;
  public String zip;
}
