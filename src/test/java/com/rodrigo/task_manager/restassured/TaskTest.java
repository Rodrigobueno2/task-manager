package com.rodrigo.task_manager.restassured;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class TaskTest {

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api";
    }

    public Integer popularTaskRetornandoId() {

        return given()
                .contentType(ContentType.JSON)
                .body("{\"title\":\"Estudar QA\",\"description\":\"Aprender a criar testes de QA\",\"status\":\"FAZENDO\"}"
        )
        .when()
                .post("/tasks")
        .then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    public void popularTasks() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"title\":\"Estudar Spring Boot\",\"description\":\"Aprender a criar APIs REST usando Spring Boot\",\"status\":\"AFAZER\"}"
                )

        .when()
                .post("/tasks")
        .then()
                .statusCode(200)
                .extract()
                .path("id");

    }

    @Test
    public void testDadoUsuarioQuandoCadastroTaskEntaoObtenhoStatusCode200() {
        given()
              .contentType(ContentType.JSON)
              .body("{ \"title\": \"Test Task\", \"description\": \"Test Description\", \"status\": \"AFAZER\" }")
        .when()
              .post("/tasks")
        .then()
              .statusCode(200) // Verifica se a tarefa foi criada com sucesso
              .body("title", equalTo("Test Task"))
              .body("status", equalTo("AFAZER"));


    }

    @Test
    public void testDadoUsuarioListarTasksEntaoObtenhoStatusCode200() {
        popularTasks();
        given()
                .contentType(ContentType.JSON)
        .when()
                .get("/tasks")
        .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].id",notNullValue())
                .body("[0].title",notNullValue())
                .body("[0].description",notNullValue())
                .body("[0].status",notNullValue())
                .body("[0].createdAt",notNullValue());
    }

    @Test
    public void testDadoUsuarioBuscarTaskPorIdEntaoObtenhoTask() {
        popularTasks();
        Integer id = popularTaskRetornandoId();
        given()
                .pathParam("id", id)
        .when()
                .get("/tasks/{id}")
        .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .contentType(ContentType.JSON)
                .body("id",equalTo(id))
                .body("title",equalTo("Estudar QA"))
                .body("description",equalTo("Aprender a criar testes de QA"))
                .body("status",equalTo("FAZENDO"));

    }

    @Test
    public void testDadoUsuarioBuscarTaskPorIdInvalidaEntaoObtenhoExcessaoStatus500() {
        Integer id = 999;

        given()
                .pathParam("id",id)
        .when()
                .get("/tasks/{id}")
        .then()
                .statusCode(404)
                .body("error",equalTo("Not Found"))
                .body("message", containsString("Task not found with id: "+id));

    }

    @Test
    public void testDadoUsuarioAtualizarUmaTaskObtenhaStatus200EAtualizaTask() {
        Integer id = popularTaskRetornandoId();

        given()
                .contentType(ContentType.JSON)
                .pathParam("id",id)
                .body("{\"title\":\"Estudar QA Atualizado\",\"description\":\"Aprender a criar testes de QA Atualizado\",\"status\":\"CONCLUIDO\"}"
                )
        .when()
                .put("/tasks/{id}")
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id",equalTo(id))
                .body("title",equalTo("Estudar QA Atualizado"))
                .body("description",equalTo("Aprender a criar testes de QA Atualizado"))
                .body("status",equalTo("CONCLUIDO"));
    }

    @Test
    public void testDadoUsuarioAtualizaUmaTaskComIdInexistente() {
        Integer id = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("id",id)
                .body("{\"title\":\"Estudar QA Atualizado\",\"description\":\"Aprender a criar testes de QA Atualizado\",\"status\":\"CONCLUIDO\"}"
                )
        .when()
                .put("/tasks/{id}")
        .then()
                .statusCode(404)
                .body("error",equalTo("Not Found"))
                .body("message", containsString("Task not found with id: "+id));

    }

    @Test
    public void testDadoUsuarioDeletaUmaTaskPorId() {
        popularTasks();
        Integer idTaskDelete = popularTaskRetornandoId();

        given()
                .pathParam("id",idTaskDelete)
        .when()
                .delete("/tasks/{id}")
        .then()
                .statusCode(204);

        given()
                .pathParam("id",idTaskDelete)
        .when()
                .get("/tasks/{id}")
        .then()
                .statusCode(404);
    }

    @Test
    public void testDadoUsuarioDeleteTaskComIdInexistenteObtenhaStatus404() {
        Integer idInexistente = 999;

        given()
                .pathParam("id",idInexistente)
        .when()
                .delete("/tasks/{id}")
        .then()
                .statusCode(404)
                .body("message",containsString("Task not found"));
    }

    @Test
    public void testPostComEnumStatusInvalido() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"title\":\"Test Task\",\"status\":\"INVALID_STATUS\"}")
        .when()
                .post("/tasks")
        .then()
                .statusCode(400)
                .body("error",equalTo("Bad Request"))
                .body("message",containsString("JSON parse error: Cannot deserialize value of type"));
    }


}
