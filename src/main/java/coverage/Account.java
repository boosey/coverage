package coverage;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

public class Account extends PanacheMongoEntity {
  public String name;
  public String address;
  public String city;
  public String state;
  public String zip;

  
}
