package coverage;

import coverage.framework.EntityInterface;
import coverage.framework.EntitySuper;
import java.util.ArrayList;
import java.util.List;

public class Account extends EntitySuper {

  public String name;
  public String address;
  public String city;
  public String state;
  public String zip;

  public List<Talent> assignedTalent;

  public Account() {
    assignedTalent = new ArrayList<Talent>();
  }

  public <T extends EntityInterface> void updateFields(T updates) {
    Account a = (Account) updates;
    this.name = a.name;
    this.address = a.address;
    this.city = a.city;
    this.state = a.state;
    this.zip = a.zip;
    return;
  }

  void assignTalent(Talent talent) {
    assignedTalent.add(talent);
    return;
  }
}
