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
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CustomersappApplicationTests {

    private static String baseUri;

    private static final String NAME_1 = "Izabela";
    private static final String NAME_2 = "Mieszko";
    private static final String SURNAME_1 = "Czajkowska";
    private static final String SURNAME_2 = "Pierwszy";
    private static final String VALID_PESEL = "11111111111";

    private static final String CUSTOMERS_ENDPOINT_PATH = "/customers";
    private static final String CUSTOMERS_VALID_PESEL_ENDPOINT_PATH = "/customers/11111111111";
    private static final String CUSTOMERS_INVALID_PESEL_ENDPOINT_PATH = "/customers/21111111111";
    private static final String CUSTOMER_1_REQUEST_BODY =
            makeCustomerRequestBody(VALID_PESEL, NAME_1, SURNAME_1);

    @BeforeAll
    static void setUp(@LocalServerPort int port) {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        baseUri = "%s:%d".formatted(RestAssured.baseURI, port);
    }

    private static String makeCustomerRequestBody(String pesel, String name, String surname) {
        return """
                {
                    "peselNumber": "%s",
                    "name": "%s",
                    "surname": "%s"
                }""".formatted(pesel, name, surname);
    }

    @Nested
    class AddCustomer {

        @Test
        void successfulOperation() {
            String locationExpected = baseUri + CUSTOMERS_ENDPOINT_PATH + "/" + VALID_PESEL;

            given().body(CUSTOMER_1_REQUEST_BODY)
                    .contentType(ContentType.JSON)
                    .when()
                    .post(CUSTOMERS_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(201)
                    .and().header("location", is(locationExpected))
                    .and().body("peselNumber", is(VALID_PESEL),
                            "name", is(NAME_1),
                            "surname", is(SURNAME_1));
        }
        @Test
        void invalidPeselNumber() {
            String toShortPeselNumber = "55555";
            String wrong_request_body = makeCustomerRequestBody(toShortPeselNumber, NAME_2, SURNAME_2);
            given().body(wrong_request_body)
                    .contentType(ContentType.JSON)
                    .when()
                    .post(CUSTOMERS_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(400);
        }

        @Test
        void cannotBeTwoCustomersWithSamePESELNumber() {

            String customerDataSamePesel = makeCustomerRequestBody(VALID_PESEL, NAME_2, SURNAME_2);

            given().body(CUSTOMER_1_REQUEST_BODY)
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
    class GetCustomer {

        @Test
        void existedCustomer() {

            given().body(CUSTOMER_1_REQUEST_BODY)
                    .contentType(ContentType.JSON)
                    .when().post(CUSTOMERS_ENDPOINT_PATH);

            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(CUSTOMERS_VALID_PESEL_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(200)
                    .and().body("peselNumber", is(VALID_PESEL),
                            "name", is(NAME_1),
                            "surname", is(SURNAME_1));
        }

        @Test
        void nonexistentCustomer() {

            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get(CUSTOMERS_INVALID_PESEL_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(404);
        }
    }

    @Nested
    class EditCustomer {
        @Test
        void cannotEditedNonexistentCustomer() {

            given().contentType(ContentType.JSON)
                    .when()
                    .body(CUSTOMER_1_REQUEST_BODY)
                    .put(CUSTOMERS_VALID_PESEL_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(404);
        }

        @Test
        void successfulEditOperation() {
            String updated_customer_data = makeCustomerRequestBody(VALID_PESEL, NAME_2, SURNAME_2);

            given().contentType(ContentType.JSON)
                    .when()
                    .body(CUSTOMER_1_REQUEST_BODY)
                    .post(CUSTOMERS_ENDPOINT_PATH);

            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .body(updated_customer_data)
                    .put(CUSTOMERS_VALID_PESEL_ENDPOINT_PATH)
                    .then().assertThat().statusCode(200)
                    .and().body(
                            "peselNumber", is(VALID_PESEL),
                            "name", is(NAME_2),
                            "surname", is(SURNAME_2));
        }

        @Test
        void modifyingPeselNumberIsNotAllowed() {
            String updated_customer_data = makeCustomerRequestBody("22222222222", NAME_2, SURNAME_2);

            given().contentType(ContentType.JSON)
                    .when()
                    .body(CUSTOMER_1_REQUEST_BODY)
                    .post(CUSTOMERS_ENDPOINT_PATH);

            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .body(updated_customer_data)
                    .put(CUSTOMERS_VALID_PESEL_ENDPOINT_PATH)
                    .then().assertThat().statusCode(403);
        }
    }

    @Nested
    class DeleteCustomer {
        @Test
        void nonexistentCustomer() {
            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete(CUSTOMERS_INVALID_PESEL_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(400);
        }

        @Test
        void existedCustomer() {

            given().body(CUSTOMER_1_REQUEST_BODY)
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
    class GetAllCustomers {

        @Test
        void nonexistentCustomers() {

            given().contentType(ContentType.JSON)
                    .when()
                    .get(CUSTOMERS_ENDPOINT_PATH)
                    .then()
                    .assertThat().statusCode(204);
        }

        @Test
        void twoExistedCustomers() {
            String secondPesel = "22222222222";
            String secondCustomerData = makeCustomerRequestBody(secondPesel, NAME_2, SURNAME_2);

            given().body(CUSTOMER_1_REQUEST_BODY)
                    .contentType(ContentType.JSON)
                    .when()
                    .post(CUSTOMERS_ENDPOINT_PATH);
            given().body(secondCustomerData)
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
                    .body("content", hasItem(
                            allOf(
                                    hasEntry("name", NAME_2),
                                    hasEntry("surname", SURNAME_2),
                                    hasEntry("peselNumber", secondPesel))))
                    .and()
                    .body("content", hasItem(
                            allOf(
                                    hasEntry("name", NAME_1),
                                    hasEntry("surname", SURNAME_1),
                                    hasEntry("peselNumber", VALID_PESEL)))
                    );
        }
    }
}
