package coverage;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import javax.inject.Inject;
import org.jboss.logging.Logger;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class AccountsTest {

  @Inject
  Logger log;

  final String accountName = "Test Account";
  final String newName = "New Account Name";

  String exampleAccountJson() {
    return new JSONObject()
      .put("name", accountName)
      .put("address", "Main St")
      .put("city", "Baton Rouge")
      .put("state", "LA")
      .put("zip", "70113")
      .toString();
  }

  @Test
  public void testAccountsEndpoint() {
    given().when().get("/accounts").then().statusCode(200);
  }

  @Test
  public void testAccountAddAndDeleteAll() {
    String a = exampleAccountJson();

    given().when().delete("/accounts").then().statusCode(200);

    String addedAccountURI = given()
      .contentType(ContentType.JSON)
      .body(a)
      .when()
      .post("/accounts")
      .then()
      .statusCode(201)
      .extract()
      .response()
      .asString();

    given()
      .when()
      .get(addedAccountURI)
      .then()
      .statusCode(200)
      .body("name", equalTo(accountName));

    given().when().delete("/accounts").then().statusCode(200);
  }

  @Test
  public void getAccount() {
    String a = exampleAccountJson();

    given().when().delete("/accounts").then().statusCode(200);

    given()
      .contentType(ContentType.JSON)
      .body(a)
      .when()
      .post("/accounts")
      .then()
      .statusCode(201);

    String id = given()
      .when()
      .get("/accounts")
      .then()
      .statusCode(200)
      .extract()
      .path("[0].id");

    given()
      .pathParam("id", id)
      .when()
      .get("/accounts/{id}")
      .then()
      .statusCode(200);

    given().when().delete("/accounts").then().statusCode(200);
  }

  @Test
  public void failOnBadAccountId() {
    given().when().delete("/accounts").then().statusCode(200);

    given()
      .pathParam("id", "60d1434f7fe4d40a3c74d8c7")
      .when()
      .get("/accounts/{id}")
      .then()
      .statusCode(404);
  }

  @Test
  public void testDeleteAccount() {
    String a = exampleAccountJson();

    given().when().delete("/accounts").then().statusCode(200);

    given()
      .contentType(ContentType.JSON)
      .body(a)
      .when()
      .post("/accounts")
      .then()
      .statusCode(201);

    String id = given()
      .when()
      .get("/accounts")
      .then()
      .statusCode(200)
      .extract()
      .path("[0].id");

    given()
      .pathParam("id", id)
      .when()
      .delete("/accounts/{id}")
      .then()
      .statusCode(200);

    given().when().delete("/accounts").then().statusCode(200);
  }

  @Test
  public void testUpdateAccount() {
    String a = exampleAccountJson();

    given().when().delete("/accounts").then().statusCode(200);

    String addedAccountURI = given()
      .contentType(ContentType.JSON)
      .body(a)
      .when()
      .post("/accounts")
      .then()
      .statusCode(201)
      .extract()
      .response()
      .asString();

    given()
      .when()
      .get(addedAccountURI)
      .then()
      .statusCode(200)
      .body("name", equalTo(accountName));

    log.info("GETTING ADDED ACCOUNT");

    String r = given()
      .when()
      .get(addedAccountURI)
      .then()
      .statusCode(200)
      .extract()
      .body()
      .asString();

    JSONObject j = new JSONObject(r);
    j.put("name", newName);
    String a1 = j.toString();

    given()
      .contentType(ContentType.JSON)
      .body(a1)
      .when()
      .put(addedAccountURI)
      .then()
      .statusCode(200);

    given()
      .when()
      .get(addedAccountURI)
      .then()
      .statusCode(200)
      .body("name", equalTo(newName));

    given().when().delete("/accounts").then().statusCode(200);
  }
}
