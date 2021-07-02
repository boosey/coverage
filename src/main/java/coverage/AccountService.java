package coverage;

import coverage.framework.AssignRelationFunction;
import coverage.framework.ServiceInterface;
import coverage.framework.ServiceSuper;
import io.smallrye.mutiny.Uni;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.bson.types.ObjectId;

@Path("/accounts")
public class AccountService extends ServiceSuper implements ServiceInterface {

  AccountService() {
    super(
      () -> Account.listAll(),
      id -> Account.findByIdOptional(id),
      () -> Account.deleteAll(),
      id -> Account.deleteById(id)
    );
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<Response> add(Account a, @Context UriInfo uriInfo) {
    return this.addEntity(a, uriInfo);
  }

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<Response> update(String id, Account updates) {
    return this.updateEntity(id, updates);
  }

  @POST
  @Path("/{accountId}/squadManager/{talentId}")
  public Uni<Response> assignTalent(
    @PathParam("accountId") String accountId,
    @PathParam("talentId") String talentId
  ) {
    AssignRelationFunction assign = (parent, childId) -> {
      Account account = (Account) parent;
      account.squadManagerId = childId;
    };

    return this.assignRelation(
        Account.findByIdOptional(new ObjectId(accountId)),
        Talent.findByIdOptional(new ObjectId(talentId)),
        assign
      );
  }
}
