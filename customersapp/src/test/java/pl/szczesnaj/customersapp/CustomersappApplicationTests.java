/*
 * Copyright (c) 2023 Joanna Szczesna
 * All rights reserved
 */

package pl.szczesnaj.customersapp;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomersappApplicationTests {

    private static final String VALID_PESEL = "11111111111";
    public static final String IZABELA = "Izabela";
    public static final String CZAJKOWSKA = "Czajkowska";
    public static final String MIESZKO = "Mieszko";
    public static final String PIERWSZY = "Pierwszy";
    private String baseUri;

    @BeforeEach
    void setUp(@LocalServerPort int port) {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        baseUri = "%s:%d".formatted(RestAssured.baseURI, port);
    }

    @Nested
    class AddUser {
        @Test
        void successfulOperation_returnAcceptedStatus() {
            given().body(userData(VALID_PESEL, IZABELA, CZAJKOWSKA)).contentType(ContentType.JSON)
                    .when().post("/customers")
                    .then().assertThat().statusCode(201);
        }
        @Test
        void secondUsersWithSamePESELNumber_returnBadRequest() {
            given().body(userData(VALID_PESEL, IZABELA, CZAJKOWSKA)).contentType(ContentType.JSON)
                    .when().post("/customers");

            given().body(userData(VALID_PESEL, MIESZKO, PIERWSZY)).contentType(ContentType.JSON)
                    .when().post("/customers")
                    .then().assertThat().statusCode(400);
        }
    }

    private static String userData(String pesel, String name, String surname) {
        return """
                {
                    "pesel": "%s",
                    "name": "%s",
                    "surname": "%s"
                }""".formatted(pesel, name, surname);
    }
}
