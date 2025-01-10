package com.rodrigo.task_manager.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class TaskTest {
    @Test
    public void testDadoUsuarioQuandoCadastroTaskEntaoObtenhoStatusCode200() {
        baseURI = "http://localhost";
        port = 8080;
        basePath = "/api";

        given()
              .contentType(ContentType.JSON)
              .body("{ \"title\": \"Test Task\", \"description\": \"Test Description\", \"status\": \"Pending\" }")
        .when()
              .post("/tasks")
        .then()
              .statusCode(200) // Verifica se a tarefa foi criada com sucesso
              .body("title", equalTo("Test Task"))
              .body("status", equalTo("Pending"));


    }
}
