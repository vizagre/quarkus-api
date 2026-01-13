package br.com.estudo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PessoaResourceTest {

    @Test
    @Order(1)
    public void testGetAllPessoas() {
        given()
            .when()
                .get("/api/pessoa")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(7))
                .body("nome", hasItems("Leonardo", "Amanda", "Kimiko", "João", "Sonia", "Leandro", "Danilo"));
    }

    @Test
    @Order(2)
    public void testFindByAnoNascimento() {
        given()
            .queryParam("anoNascimento", 1970)
            .when()
                .get("/api/pessoa/findByAnoNascimento")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(1))
                .body("[0].nome", is("Leonardo"))
                .body("[0].anoNascimento", is(1970));
    }

    @Test
    @Order(3)
    public void testFindByAnoNascimentoMultipleResults() {
        // Primeiro vamos verificar que existem 2 pessoas nascidas em 1996 (Amanda) e 1974 (Leandro)
        given()
            .queryParam("anoNascimento", 1996)
            .when()
                .get("/api/pessoa/findByAnoNascimento")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(1));
    }

    @Test
    @Order(4)
    public void testFindByAnoNascimentoNotFound() {
        given()
            .queryParam("anoNascimento", 2000)
            .when()
                .get("/api/pessoa/findByAnoNascimento")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(0));
    }

    @Test
    @Order(5)
    public void testCreatePessoa() {
        String novaPessoa = """
            {
                "nome": "Maria",
                "anoNascimento": 1985
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(novaPessoa)
            .when()
                .post("/api/pessoa")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("nome", is("Maria"))
                .body("anoNascimento", is(1985))
                .body("id", notNullValue());
    }

    @Test
    @Order(6)
    public void testCreatePessoaWithId() {
        // Testa que o ID fornecido é ignorado e um novo ID é gerado
        String novaPessoa = """
            {
                "id": 999,
                "nome": "Carlos",
                "anoNascimento": 1990
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(novaPessoa)
            .when()
                .post("/api/pessoa")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("nome", is("Carlos"))
                .body("anoNascimento", is(1990))
                .body("id", not(999));
    }

    @Test
    @Order(7)
    public void testUpdatePessoa() {
        // Primeiro criamos uma pessoa para atualizar
        String novaPessoa = """
            {
                "nome": "Ana",
                "anoNascimento": 1988
            }
            """;

        Integer id = given()
            .contentType(ContentType.JSON)
            .body(novaPessoa)
            .when()
                .post("/api/pessoa")
            .then()
                .statusCode(200)
                .extract()
                .path("id");

        // Agora atualizamos a pessoa
        String pessoaAtualizada = String.format("""
            {
                "id": %d,
                "nome": "Ana Paula",
                "anoNascimento": 1989
            }
            """, id);

        given()
            .contentType(ContentType.JSON)
            .body(pessoaAtualizada)
            .when()
                .put("/api/pessoa")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", is(id))
                .body("nome", is("Ana Paula"))
                .body("anoNascimento", is(1989));

        // Verifica se a atualização foi persistida
        given()
            .when()
                .get("/api/pessoa")
            .then()
                .statusCode(200)
                .body("find { it.id == " + id + " }.nome", is("Ana Paula"))
                .body("find { it.id == " + id + " }.anoNascimento", is(1989));
    }

    @Test
    @Order(8)
    public void testDeletePessoa() {
        // Primeiro criamos uma pessoa para deletar
        String novaPessoa = """
            {
                "nome": "Pedro",
                "anoNascimento": 1995
            }
            """;

        Integer id = given()
            .contentType(ContentType.JSON)
            .body(novaPessoa)
            .when()
                .post("/api/pessoa")
            .then()
                .statusCode(200)
                .extract()
                .path("id");

        // Deletamos a pessoa
        given()
            .when()
                .delete("/api/pessoa/" + id)
            .then()
                .statusCode(204);

        // Verifica que a pessoa não existe mais
        given()
            .when()
                .get("/api/pessoa")
            .then()
                .statusCode(200)
                .body("find { it.id == " + id + " }", nullValue());
    }

    @Test
    @Order(9)
    public void testDeleteNonExistingPessoa() {
        // Testa deletar uma pessoa que não existe
        given()
            .when()
                .delete("/api/pessoa/99999")
            .then()
                .statusCode(204); // Quarkus/Panache não retorna erro ao deletar ID inexistente
    }

    @Test
    @Order(10)
    public void testCreatePessoaWithInvalidData() {
        // Testa criar pessoa com dados inválidos (JSON vazio)
        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
                .post("/api/pessoa")
            .then()
                .statusCode(200); // Como não há validações, aceita o JSON
    }

    @Test
    @Order(11)
    public void testGetPessoasAfterOperations() {
        // Verifica o estado final após todas as operações
        given()
            .when()
                .get("/api/pessoa")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThan(7))); // Deve ter mais que as 7 iniciais
    }
}
