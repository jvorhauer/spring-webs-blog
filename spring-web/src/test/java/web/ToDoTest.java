package web;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ToDoTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate rest;

  private String url;

  @PostConstruct
  void init() {
    url = "http://localhost:" + port + "/todos";
  }

  @Test
  void getShouldReturnEmptyList() {
    System.out.println("url: " + url);
    assertThat(rest.getForObject("http://localhost:" + port + "/todos", List.class))
      .isNotNull()
      .hasSize(0);
  }

  @Test
  void insertOneAndRetrieveList() {
    final String json = "{\"title\":\"test\",\"priority\":\"LOW\",\"status\":\"TODO\"}";
    HttpEntity<String> he = new HttpEntity<>(json, headers());
    final ToDoItem response = rest.postForObject("http://localhost:" + port + "/todos", he, ToDoItem.class);
    assertThat(response).isNotNull();
    assertThat(response.getTitle()).isEqualTo("test");
  }

  @Test
  void insertOneAndThenUpdateAndRetrieveList() {
    final String json = "{\"title\":\"test\",\"priority\":\"LOW\",\"status\":\"TODO\"}";
    HttpEntity<String> he = new HttpEntity<>(json, headers());
    ToDoItem todo = rest.postForObject("http://localhost:" + port + "/todos", he, ToDoItem.class);
    assertThat(todo).isNotNull();
    assertThat(todo.getId()).isNotNull();
    assertThat(todo.getStatus()).isEqualTo(Status.TODO);
    final Long id = todo.getId();

    rest.put("http://localhost:" + port + "/todos/" + id + "/DOING", null);
    ToDoItem[] todos = rest.getForObject("http://localhost:" + port + "/todos", ToDoItem[].class);
    assertThat(todos).isNotNull();
    assertThat(todos).hasSize(1);
    todo = todos[0];
    assertThat(todo).isNotNull();
    assertThat(todo.getStatus()).isEqualTo(Status.DOING);
  }

  @Test
  void insertOneAndThenUpdateButWrongType() {
    final String json = "{\"title\":\"test\",\"priority\":\"LOW\",\"status\":\"TODO\"}";
    HttpEntity<String> he = new HttpEntity<>(json, headers());
    ToDoItem todo = rest.postForObject("http://localhost:" + port + "/todos", he, ToDoItem.class);
    assertThat(todo).isNotNull();
    assertThat(todo.getId()).isNotNull();
    assertThat(todo.getStatus()).isEqualTo(Status.TODO);
    final Long id = todo.getId();

    rest.put("http://localhost:" + port + "/todos/" + id + "/NOPE", null);
    ToDoItem[] todos = rest.getForObject("http://localhost:" + port + "/todos", ToDoItem[].class);
    assertThat(todos).isNotNull();
    assertThat(todos).hasSize(1);
    todo = todos[0];
    assertThat(todo).isNotNull();
    assertThat(todo.getStatus()).isEqualTo(Status.TODO);
  }

  @Test

  private HttpHeaders headers() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }


  @BeforeEach
  void wipe() {
    rest.delete("http://localhost:" + port + "/todos");
  }
}
