package coverage;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

public class ServiceSuper {

  public <T extends ReactivePanacheMongoEntity> Uni<Response> list(
    Uni<List<T>> list
  ) {
    return list
      .onItem()
      .transform(items -> Response.ok().entity(items).build())
      .onFailure()
      .recoverWithItem(
        error ->
          Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build()
      );
  }

  public <T extends ReactivePanacheMongoEntity> Uni<Response> findById(
    Uni<Optional<T>> entity
  ) {
    return entity
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

  public Uni<Response> add(ReactivePanacheMongoEntity entity, UriInfo uriInfo) {
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

  public Uni<Response> delete(Uni<Long> deleteUni) {
    return deleteUni
      .onItem()
      .transform(count -> Response.ok().entity(count).build())
      .onFailure()
      .recoverWithItem(Response.status(Status.INTERNAL_SERVER_ERROR).build());
  }

  public Uni<Response> deleteById(Uni<Boolean> deleteUni) {
    return deleteUni
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

  public <T extends EntitySuper> Uni<Response> update(
    Uni<Optional<T>> entity,
    T updates
  ) {
    return entity
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
              .item(Response.status(Status.NOT_FOUND).entity(entity).build());
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
