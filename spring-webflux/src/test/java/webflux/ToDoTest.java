package webflux;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static webflux.ToDoItem.Priority.LOW;
import static webflux.ToDoItem.Status.DOING;
import static webflux.ToDoItem.Status.TODO;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class ToDoTest {

  @LocalServerPort
  private int port;

  @Autowired
  private ExchangeStrategies exchangeStrategies;

  private WebClient webClient;


  @Test
  void getShouldReturnEmptyList() {
    assertThat(webClient.get()
                 .uri("/todos")
                 .retrieve()
                 .bodyToFlux(ToDoItem.class)
                 .collectList()
                 .block())
      .isNotNull()
      .hasSize(0);
  }

  @Test
  void addTest() {
    final ToDoItem td = new ToDoItem("test", LOW, TODO);
    var res = webClient.post()
                .uri("/todos")
                .body(Mono.just(td), ToDoItem.class)
                .retrieve()
                .bodyToMono(ToDoItem.class)
                .block();
    assertThat(res).isNotNull();
    assertThat(res.getId()).isGreaterThanOrEqualTo(0L);
    assertThat(res.getTitle()).isEqualTo("test");
  }

  @Test
  void addAndRetrieveList() {
    addTest();
    var res = webClient.get()
                .uri("/todos")
                .retrieve()
                .bodyToFlux(ToDoItem.class);
    assertThat(res).isNotNull();
    assertThat(res.collectList().block()).hasSize(1);
  }

  @Test
  void insertOneAndThenUpdateAndRetrieveList() {
    addTest();
    var res = webClient.get()
                .uri("/todos")
                .retrieve()
                .bodyToFlux(ToDoItem.class);
    assertThat(res).isNotNull();
    var list = res.collectList().block();
    assertThat(list).isNotNull();
    assertThat(list).hasSize(1);
    var td = list.iterator().next();
    assertThat(td).isNotNull();
    assertThat(td.getTitle()).isEqualTo("test");
    td.setStatus(DOING);

    td = webClient.put()
           .uri("/todo")
           .body(Mono.just(td), ToDoItem.class)
           .retrieve()
           .bodyToMono(ToDoItem.class)
           .block();
    assertThat(td).isNotNull();
    assertThat(td.getStatus()).isEqualTo(DOING);
  }


  @BeforeEach
  void wipe() {
    webClient = WebClient.builder()
                  .baseUrl("http://localhost:" + port)
                  .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                  .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                  .exchangeStrategies(exchangeStrategies)
                  .build();

    log.info("wipe");
    webClient.delete().uri("/todos").retrieve().toBodilessEntity().block();
  }
}
