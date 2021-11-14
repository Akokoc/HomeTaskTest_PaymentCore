import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class M3OPlatformTest {
    private static String userId;
    private static final String baseUrl = "https://api.m3o.com/v1/user";
    private static final String token = "ZDIyOWY3MWMtZjBlNi00MjViLTg3MGUtYTE2OWJlMzYyN2Vi";

    static {
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
    }

    /*
        Happy test for POST method which creates the User.
    */
    @Test
    @Order(1)
    public void testCreateUser() {
        String url = baseUrl + "/Create";

        String body = "{\"username\": \"test_1\", \"email\": \"test_1@gmail.com\" ," +
                " \"password\": \"secretPass123\"}";
        Response response = RestAssured.given()
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(body)
                .post(url)
                .then().statusCode(200).extract().response();

        JsonPath path = new JsonPath(response.asString());
        userId = path.getString("account.id");

        String url1 = baseUrl + "/Read";
        String body1 = String.format("{\"id\": \"%s\"}", userId);
        RestAssured.given()
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(body1)
                .post(url1)
                .then().statusCode(200).body(equalTo(response.getBody().print()));
    }

    /*
        Happy test for POST method which updates the User email.
    */
    @Test
    @Order(2)
    public void testUpdateUserEmail() {
        String url = baseUrl + "/Update";
        String body = String.format("{\"id\": \"%s\", \"email\": \"test_2@gmail.com\"}", userId);
        RestAssured.given()
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(body)
                .post(url)
                .then().statusCode(200);

        String url1 = baseUrl + "/Read";
        String body1 = String.format("{\"id\": \"%s\"}", userId);
        RestAssured.registerParser("text/plain", Parser.JSON);
        RestAssured.given()
                .header("accept", "application/json")
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(body1)
                .post(url1)
                .then().statusCode(200).body("account.email", equalTo("test_2@gmail.com"));
    }

    /*
        Negative test for POST method which creates the User who is already created.
    */
    @Test
    @Order(3)
    public void testCreateUserWhoIsAlreadyCreated() {
        String url = baseUrl + "/Create";

        String body = String.format("{\"id\": \"%s\", \"username\": \"test_1\", \"email\": \"test_1@gmail.com\" ," +
                " \"password\": \"secretPass123\"}", userId);
        RestAssured.given()
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(body)
                .post(url)
                .then().statusCode(400).body(equalTo("{\"Id\":\"create.username-check\",\"Code\":400," +
                "\"Detail\":\"username already exists\",\"Status\":\"Bad Request\"}"));

        String url1 = baseUrl + "/Read";
        String body1 = String.format("{\"id\": \"%s\"}", userId);
        RestAssured.given()
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(body1)
                .post(url1)
                .then().statusCode(200);
    }

    /*
        Negative test for POST method which creates the User with invalid email.
     */
    @Test
    @Order(4)
    public void testCreateUserWithWrongEmail() {
        String url = baseUrl + "/Create";

        String body = "{\"username\": \"test_w\", \"email\": \"@example.com\" ," +
                " \"password\": \"mySecretPass123\"}";
        RestAssured.given()
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(body)
                .post(url)
                .then().statusCode(400).body(equalTo("{\"Id\":\"create.email-format-check\",\"Code\":400," +
                "\"Detail\":\"email has wrong format\",\"Status\":\"Bad Request\"}"));
    }

    /*
        Happy test for DELETE method which removes the User by userId.
        When the userId is removed, the method should return 200 OK with body "{\"account\":null}".
        Currently it returns 500.
    */
    @Test
    @Order(5)
    public void testDelete() {
        String url = baseUrl + "/Delete";
        String body = String.format("{\"id\": \"%s\"}", userId);

        RestAssured.given()
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(body)
                .delete(url)
                .then().statusCode(200);

        String url1 = baseUrl + "/Read";
        String body1 = String.format("{\"id\": \"%s\"}", userId);
        RestAssured.given()
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(body1)
                .post(url1)
                .then().statusCode(500); //"{\"account\":null}" 200
    }

}
