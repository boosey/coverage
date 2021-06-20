package coverage;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import javax.json.Json;
import javax.json.JsonObject;

import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
public class AccountsTest {

    @Test
    public void testAccountsEndpoint() {
      Account.deleteAll();      
        given()
          .when().get("/accounts")
          .then()
             .statusCode(200)
             .body(is("[]"));
    }

    @Test
    public void testAccountAdd() {
      Account.deleteAll();

      JsonObject a = Json.createObjectBuilder()
                        .add("name", "Test Account")
                        .add("address", "Main St")
                        .add("city", "Baton Rouge")
                        .add("state", "LA")
                        .add("zip", "70113")
                        .build();

      given()
        .contentType(ContentType.JSON)
        .body(a.toString())
        .when().post("/accounts")
        .then()
          .statusCode(200);

      given()
        .when().get("/accounts")
        .then()
            .statusCode(200)
            .body("[0].name", equalTo("Test Account"));    

      Account.deleteAll();
    }

}