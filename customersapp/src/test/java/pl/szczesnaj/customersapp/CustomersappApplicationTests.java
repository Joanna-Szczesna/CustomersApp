/*
 * Copyright (c) 2024 Joanna Szczesna
 * All rights reserved
 */

package pl.szczesnaj.customersapp;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.google.common.truth.Truth.assertThat;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomersappApplicationTests {

    private static String baseUri;
    private static HttpClient client;

    private static final String MIESZKO = "Mieszko";
    private static final String IZABELA = "Izabela";
    private static final String PIERWSZY = "Pierwszy";
    private static final String CZAJKOWSKA = "Czajkowska";
    private static final String VALID_PESEL = "11111111111";

    private static final String NAME_EXPECTED = getFormatted("name", IZABELA);
    private static final String SURNAME_EXPECTED = getFormatted("surname", CZAJKOWSKA);
    private static final String PESEL_NUMBER_EXPECTED = getFormatted("peselNumber", VALID_PESEL);
    private static final String CUSTOMER_REQUEST_BODY = getCustomerRequestBody(VALID_PESEL, IZABELA, CZAJKOWSKA);

    private static final String CUSTOMERS_ENDPOINT_PATH = "/customers";
    private static final String CUSTOMERS_VALID_PESEL_ENDPOINT_PATH = "/customers/11111111111";
    private static final String CUSTOMERS_INVALID_PESEL_ENDPOINT_PATH = "/customers/21111111111";

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final HttpResponse.BodyHandler<String> RESPONSE_BODY_HANDLER = HttpResponse.BodyHandlers.ofString();

    @BeforeAll
    static void setUp(@LocalServerPort int port) {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        baseUri = "%s:%d".formatted(RestAssured.baseURI, port);
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    void cleanDB() {
        // cleanDB
    }

    private static String getCustomerRequestBody(String pesel, String name, String surname) {
        return """
                {
                    "peselNumber": "%s",
                    "name": "%s",
                    "surname": "%s"
                }""".formatted(pesel, name, surname);
    }

    private static String getFormatted(String property, String value) {
        return "\"%s\":\"%s\"".formatted(property, value);
    }

    @Nested
    class AddCustomer_POST {

        @Test
        void successfulOperation_returnCustomer_customerLocation_CreatedStatus_201() throws IOException, InterruptedException {

            HttpRequest postRequest = buildPostRequest(CUSTOMER_REQUEST_BODY, CUSTOMERS_ENDPOINT_PATH);

            HttpResponse<String> response = client.send(postRequest, RESPONSE_BODY_HANDLER);

            String locationResponse = response.headers().map().get("location").getFirst();
            String locationExpected = baseUri + CUSTOMERS_ENDPOINT_PATH + "/" + VALID_PESEL;
            int statusCodeResponse = response.statusCode();
            String bodyResponse = response.body();

            assertEquals(201, statusCodeResponse);
            assertEquals(locationExpected, locationResponse);

            assertThat(bodyResponse).contains(PESEL_NUMBER_EXPECTED);
            assertThat(bodyResponse).contains(NAME_EXPECTED);
            assertThat(bodyResponse).contains(SURNAME_EXPECTED);
        }

        @Test
        void secondUsersWithSamePESELNumber_returnConflict_409() {

            String customerDataSamePesel = getCustomerRequestBody(VALID_PESEL, MIESZKO, PIERWSZY);

            given().body(CUSTOMER_REQUEST_BODY)
                    .contentType(ContentType.JSON)
                    .when().post(CUSTOMERS_ENDPOINT_PATH);

            given().body(customerDataSamePesel)
                    .contentType(ContentType.JSON)
                    .when()
                    .post(CUSTOMERS_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(409);
        }
    }

    @Nested
    class GetCustomer_GET {

        @Test
        void existedCustomer_returnCustomer_statusCodeOK_200() {

            given().body(CUSTOMER_REQUEST_BODY)
                    .contentType(ContentType.JSON)
                    .when().post(CUSTOMERS_ENDPOINT_PATH);

            Response response = given()
                    .contentType(ContentType.JSON)
                    .when().get(CUSTOMERS_VALID_PESEL_ENDPOINT_PATH);

            String bodyResponse = response.body().print();

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(bodyResponse).contains(PESEL_NUMBER_EXPECTED);
            assertThat(bodyResponse).contains(NAME_EXPECTED);
            assertThat(bodyResponse).contains(SURNAME_EXPECTED);
        }

        @Test
        void nonExistedCustomer_returnStatusNotFound_404() {

            given()
                    .contentType(ContentType.JSON)
                    .when().get(CUSTOMERS_INVALID_PESEL_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(404);
        }
    }

    @Nested
    class EditCustomer_PUT {
        @Test
        void nonExistedCustomer_returnStatusNotFound_404() {

            given().contentType(ContentType.JSON)
                    .when()
                    .body(CUSTOMER_REQUEST_BODY)
                    .put(CUSTOMERS_VALID_PESEL_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(404);
        }

        @Test
        void existedCustomer_returnUpdatedCustomer_StatusOK_200() {
            String updated_customer_data = getCustomerRequestBody(VALID_PESEL, MIESZKO, PIERWSZY);

            given().contentType(ContentType.JSON)
                    .when()
                    .body(CUSTOMER_REQUEST_BODY)
                    .post(CUSTOMERS_ENDPOINT_PATH);

            Response putResponse = given()
                    .contentType(ContentType.JSON)
                    .when()
                    .body(updated_customer_data)
                    .put(CUSTOMERS_VALID_PESEL_ENDPOINT_PATH);

            String bodyResponse = putResponse.body().print();
            String surnameExpected = getFormatted("surname", PIERWSZY);
            String nameExpected = getFormatted("name", MIESZKO);
            String peselNumberExpected = getFormatted("peselNumber", VALID_PESEL);

            assertThat(putResponse.statusCode()).isEqualTo(200);
            assertThat(bodyResponse).contains(peselNumberExpected);
            assertThat(bodyResponse).contains(nameExpected);
            assertThat(bodyResponse).contains(surnameExpected);
        }
    }

    @Nested
    class DeleteCustomer_DELETE {
        @Test
        void nonExistedCustomer_returnStatusBadRequest_400() {
            given()
                    .contentType(ContentType.JSON)
                    .when().delete(CUSTOMERS_INVALID_PESEL_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(400);
        }

        @Test
        void existedCustomer_returnStatusNoContent_204() {

            given().body(CUSTOMER_REQUEST_BODY)
                    .contentType(ContentType.JSON)
                    .when().post(CUSTOMERS_ENDPOINT_PATH);

            given().contentType(ContentType.JSON)
                    .when().delete(CUSTOMERS_VALID_PESEL_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(204);
        }
    }

    @Nested
    class GetAllCustomers_GET {

        @Test
        void nonExistedCustomers_return_statusNoContent_204() {

            given().contentType(ContentType.JSON)
                    .when().get(CUSTOMERS_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(204);
        }

        @Test
        void threeExistedCustomers_returnStatusOK_200() {
            String secondPesel = "22222222222";
            String thirdPesel = "33333333333";

            String secondCustomerData = getCustomerRequestBody(secondPesel, MIESZKO, PIERWSZY);
            String thirdCustomerData = getCustomerRequestBody(thirdPesel, "Bob", "Snail");

            String secondNameExpected = getFormatted("name", MIESZKO);
            String secondSurnameExpected = getFormatted("surname", PIERWSZY);
            String secondPeselNumberExpected = getFormatted("peselNumber", secondPesel);

            String thirdNameExpected = getFormatted("name", "Bob");
            String thirdSurnameExpected = getFormatted("surname", "Snail");
            String thirdPeselNumberExpected = getFormatted("peselNumber", thirdPesel);


            given().body(CUSTOMER_REQUEST_BODY)
                    .contentType(ContentType.JSON)
                    .when().post(CUSTOMERS_ENDPOINT_PATH);
            given().body(secondCustomerData)
                    .contentType(ContentType.JSON)
                    .when().post(CUSTOMERS_ENDPOINT_PATH);
            given().body(thirdCustomerData)
                    .contentType(ContentType.JSON)
                    .when().post(CUSTOMERS_ENDPOINT_PATH);


            Response response = given()
                    .contentType(ContentType.JSON)

                    .when().get(CUSTOMERS_ENDPOINT_PATH);

            String bodyResponse = response.body().print();

            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(bodyResponse).contains(PESEL_NUMBER_EXPECTED);
            assertThat(bodyResponse).contains(NAME_EXPECTED);
            assertThat(bodyResponse).contains(SURNAME_EXPECTED);

            assertThat(bodyResponse).contains(secondPeselNumberExpected);
            assertThat(bodyResponse).contains(secondNameExpected);
            assertThat(bodyResponse).contains(secondSurnameExpected);

            assertThat(bodyResponse).contains(thirdPeselNumberExpected);
            assertThat(bodyResponse).contains(thirdNameExpected);
            assertThat(bodyResponse).contains(thirdSurnameExpected);
        }
    }

    private HttpRequest buildPostRequest(String customer_data, String endpointPath) {
        return HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(URI.create(baseUri + endpointPath))
                .POST(HttpRequest.BodyPublishers.ofString(customer_data))
                .build();
    }
}
