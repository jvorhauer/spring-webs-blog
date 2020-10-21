package jafu;

import static jafu.ToDoItem.Priority.LOW;
import static jafu.ToDoItem.Status.TODO;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

public class ApplicationTest {

  private final WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build();
  private static ConfigurableApplicationContext context;

  @BeforeAll
  static void beforeAll() {
    context = Application.app.run("test");
  }

  @Test
  void allTest() {
    client.get().uri("/todos").exchange().expectStatus().is2xxSuccessful();
  }

  @Test
  void addTest() {
    client.post()
      .uri("/todos")
      .body(Mono.just(new ToDoItem("test", LOW, TODO)), ToDoItem.class)
      .exchange()
      .expectStatus().is2xxSuccessful();

    client.get()
      .uri("/todos")
      .exchange()
      .expectStatus().is2xxSuccessful()
      .expectBodyList(ToDoItem.class).hasSize(1);
  }

  @AfterAll
  static void afterAll() {
    context.stop();
  }
}
