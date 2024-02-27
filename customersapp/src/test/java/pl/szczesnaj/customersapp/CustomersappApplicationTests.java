/*
 * Copyright (c) 2024 Joanna Szczesna
 * All rights reserved
 */

package pl.szczesnaj.customersapp;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CustomersappApplicationTests {

    private static String baseUri;

    private static final String MIESZKO = "Mieszko";
    private static final String IZABELA = "Izabela";
    private static final String PIERWSZY = "Pierwszy";
    private static final String CZAJKOWSKA = "Czajkowska";
    private static final String VALID_PESEL = "11111111111";

    private static final String CUSTOMERS_ENDPOINT_PATH = "/customers";
    private static final String CUSTOMERS_VALID_PESEL_ENDPOINT_PATH = "/customers/11111111111";
    private static final String CUSTOMERS_INVALID_PESEL_ENDPOINT_PATH = "/customers/21111111111";
    private static final String CUSTOMER_REQUEST_BODY_IZABELA_CZAJKOWSKA =
            getCustomerRequestBody(VALID_PESEL, IZABELA, CZAJKOWSKA);

    @BeforeAll
    static void setUp(@LocalServerPort int port) {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        baseUri = "%s:%d".formatted(RestAssured.baseURI, port);
    }

    private static String getCustomerRequestBody(String pesel, String name, String surname) {
        return """
                {
                    "peselNumber": "%s",
                    "name": "%s",
                    "surname": "%s"
                }""".formatted(pesel, name, surname);
    }

    @Nested
    class AddCustomer_POST {

        @Test
        void successfulOperation_returnCustomer_customerLocation_CreatedStatus_201() {
            String locationExpected = baseUri + CUSTOMERS_ENDPOINT_PATH + "/" + VALID_PESEL;

            given().body(CUSTOMER_REQUEST_BODY_IZABELA_CZAJKOWSKA)
                    .contentType(ContentType.JSON)
                    .when()
                    .post(CUSTOMERS_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(201)
                    .and().header("location", is(locationExpected))
                    .and().body("peselNumber", is(VALID_PESEL),
                            "name", is(IZABELA),
                            "surname", is(CZAJKOWSKA));
        }

        @Test
        void secondUsersWithSamePESELNumber_returnConflict_409() {

            String customerDataSamePesel = getCustomerRequestBody(VALID_PESEL, MIESZKO, PIERWSZY);

            given().body(CUSTOMER_REQUEST_BODY_IZABELA_CZAJKOWSKA)
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

            given().body(CUSTOMER_REQUEST_BODY_IZABELA_CZAJKOWSKA)
                    .contentType(ContentType.JSON)
                    .when().post(CUSTOMERS_ENDPOINT_PATH);

            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(CUSTOMERS_VALID_PESEL_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(200)
                    .and().body("peselNumber", is(VALID_PESEL),
                            "name", is(IZABELA),
                            "surname", is(CZAJKOWSKA));
        }

        @Test
        void nonExistedCustomer_returnStatusNotFound_404() {

            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(CUSTOMERS_INVALID_PESEL_ENDPOINT_PATH)
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
                    .body(CUSTOMER_REQUEST_BODY_IZABELA_CZAJKOWSKA)
                    .put(CUSTOMERS_VALID_PESEL_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(404);
        }

        @Test
        void existedCustomer_returnUpdatedCustomer_StatusOK_200() {
            String updated_customer_data = getCustomerRequestBody(VALID_PESEL, MIESZKO, PIERWSZY);

            given().contentType(ContentType.JSON)
                    .when()
                    .body(CUSTOMER_REQUEST_BODY_IZABELA_CZAJKOWSKA)
                    .post(CUSTOMERS_ENDPOINT_PATH);

            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .body(updated_customer_data)
                    .put(CUSTOMERS_VALID_PESEL_ENDPOINT_PATH)
                    .then().assertThat().statusCode(200)
                    .and().body(
                            "peselNumber", is(VALID_PESEL),
                            "name", is(MIESZKO),
                            "surname", is(PIERWSZY));
        }
    }

    @Nested
    class DeleteCustomer_DELETE {
        @Test
        void nonExistedCustomer_returnStatusBadRequest_400() {
            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete(CUSTOMERS_INVALID_PESEL_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(400);
        }

        @Test
        void existedCustomer_returnStatusNoContent_204() {

            given().body(CUSTOMER_REQUEST_BODY_IZABELA_CZAJKOWSKA)
                    .contentType(ContentType.JSON)
                    .when()
                    .post(CUSTOMERS_ENDPOINT_PATH);

            given().contentType(ContentType.JSON)
                    .when()
                    .delete(CUSTOMERS_VALID_PESEL_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(204);
        }
    }

    @Nested
    class GetAllCustomers_GET {

        @Test
        void nonExistedCustomers_return_statusNoContent_204() {

            given().contentType(ContentType.JSON)
                    .when()
                    .get(CUSTOMERS_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(204);
        }

        @Test
        void threeExistedCustomers_returnStatusOK_200() {
            String nameBob = "Bob";
            String surnameSnail = "Snail";
            String secondPesel = "22222222222";
            String thirdPesel = "33333333333";

            String secondCustomerData = getCustomerRequestBody(secondPesel, MIESZKO, PIERWSZY);
            String thirdCustomerData = getCustomerRequestBody(thirdPesel, nameBob, surnameSnail);

            given().body(CUSTOMER_REQUEST_BODY_IZABELA_CZAJKOWSKA)
                    .contentType(ContentType.JSON)
                    .when()
                    .post(CUSTOMERS_ENDPOINT_PATH);
            given().body(secondCustomerData)
                    .contentType(ContentType.JSON)
                    .when()
                    .post(CUSTOMERS_ENDPOINT_PATH);
            given().body(thirdCustomerData)
                    .contentType(ContentType.JSON)
                    .when()
                    .post(CUSTOMERS_ENDPOINT_PATH);
//            String printedResponse = """
//                    {"content":[
//                    {"id":1,"peselNumber":"11111111111","name":"Izabela","surname":"Czajkowska","contacts":null},
//                    {"id":2,"peselNumber":"22222222222","name":"Mieszko","surname":"Pierwszy","contacts":null},
//                    {"id":3,"peselNumber":"33333333333","name":"Bob","surname":"Snail","contacts":null}
//                    ],
//                    "pageable":{"pageNumber":0,"pageSize":5,"sort":{"empty":false,"sorted":true,"unsorted":false},
//                    "offset":0,"paged":true,"unpaged":false},
//                    "last":true,"totalElements":3,"totalPages":1,"first":true,"size":5,"number":0,
//                    "sort":{"empty":false,"sorted":true,"unsorted":false},"numberOfElements":3,"empty":false}
//                    """;

            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(CUSTOMERS_ENDPOINT_PATH)
                    .then()
                    .statusCode(200)
                    .and()
                    .body("content.name", hasItems(IZABELA, MIESZKO, nameBob),
                            "content.surname", hasItems(CZAJKOWSKA, PIERWSZY, surnameSnail),
                            "content.peselNumber", hasItems(VALID_PESEL, secondPesel, thirdPesel));
        }
    }
}
