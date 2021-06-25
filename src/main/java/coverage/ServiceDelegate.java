package coverage;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.bson.types.ObjectId;

@ApplicationScoped
public class ServiceDelegate {

  public <S extends ServiceInterface> Uni<Response> list(S svc) {
    return svc
      .listUni()
      .onItem()
      .transform(items -> Response.ok().entity(items).build())
      .onFailure()
      .recoverWithItem(
        error ->
          Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build()
      );
  }

  public <S extends ServiceInterface> Uni<Response> findById(S svc, String id) {
    return svc
      .findByIdOptionalUni(new ObjectId(id))
      .onItem()
      .transform(
        item -> {
          if (item.isPresent()) {
            return Response.ok().entity(item.get()).build();
          } else {
            return Response.status(Status.NOT_FOUND).build();
          }
        }
      )
      .onFailure()
      .recoverWithItem(
        err -> Response.status(Status.INTERNAL_SERVER_ERROR).entity(err).build()
      );
  }

  public <S extends ServiceInterface> Uni<Response> add(
    S svc,
    ReactivePanacheMongoEntity entity,
    UriInfo uriInfo
  ) {
    return entity
      .persist()
      .onItem()
      .transform(
        v ->
          Response
            .status(Status.CREATED)
            .entity(
              uriInfo
                .getAbsolutePathBuilder()
                .segment(entity.id.toString())
                .build()
                .toString()
            )
            .build()
      )
      .onFailure()
      .recoverWithItem(
        err -> Response.status(Status.INTERNAL_SERVER_ERROR).entity(err).build()
      );
  }

  public <S extends ServiceInterface> Uni<Response> delete(S svc) {
    return svc
      .deleteAllUni()
      .onItem()
      .transform(count -> Response.ok().entity(count).build())
      .onFailure()
      .recoverWithItem(Response.status(Status.INTERNAL_SERVER_ERROR).build());
  }

  public <S extends ServiceInterface> Uni<Response> deleteById(
    S svc,
    String id
  ) {
    return svc
      .deleteByIdUni(new ObjectId(id))
      .onItem()
      .transform(
        succeeded -> {
          if (succeeded) {
            return Response.ok().build();
          } else {
            return Response.status(Status.NOT_FOUND).build();
          }
        }
      )
      .onFailure()
      .recoverWithItem(Response.status(Status.INTERNAL_SERVER_ERROR).build());
  }

  public <E extends EntitySuper, S extends ServiceInterface> Uni<Response> update(
    S svc,
    String id,
    E updates
  ) {
    return svc
      .findByIdOptionalUni(new ObjectId(id))
      .onItem()
      .transformToUni(
        // Received an optional from the find, so we have to check if it is present
        itemOptional -> {
          if (itemOptional.isPresent()) {
            /* 
            We found the account. Get the account object from the optional. a1 is linked to the database and it has all the current values of the record. 
            */
            EntitySuper item = itemOptional.get();

            /* 
            Update the fields of a1 with all the fields from the account passed into the method (a)
            */

            item.updateFields(updates);

            /* 
            Now we can update the database with the values in the linked account (a1). This is a reactive call, and so we have to handle its completion (onItem) and return the appropriate Response. 
            
            If something went wrong (onFailure), we know it cannot be that the record doesn't exist because we are only in this code if it does exist. So, the error must be something more catastrphic. This onFailure could occur if the network crashed in-between the call to findByIdOptional and a1.update. 
            */
            return item
              .update()
              .onItem()
              .transform(v -> Response.ok().build())
              .onFailure()
              .recoverWithItem(
                error ->
                  Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .build()
              );
          } else {
            /* 
            This else clause gets executed only if the findByIdOptional returns an Optional with no Account inside - meaning the accountId passed in does not exist in the database. 
            */
            return Uni
              .createFrom()
              .item(Response.status(Status.NOT_FOUND).build());
          }
        }
      )
      /* 
      This onFailure clause is on the findByIdOptional call. Remember, that method will always return an Optional (with a value present or not) unless there is some catastrophic error. 
      */
      .onFailure()
      .recoverWithItem(
        err -> Response.status(Status.INTERNAL_SERVER_ERROR).entity(err).build()
      );
  }
}
