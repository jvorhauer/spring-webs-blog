package web;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableTransactionManagement
@Slf4j
public class Application {

  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }


  @Bean
  public RouterFunction<ServerResponse> routes(final ToDoService service) {
    final RequestPredicate rp = accept(MediaType.APPLICATION_JSON).and(contentType(MediaType.APPLICATION_JSON));
    return route()
             .POST("/todos", rp, sr -> add(sr.bodyToMono(ToDoItem.class), service))
             .GET("/todos", rp, sr -> all(service))
             .GET("/todo/{id}", rp, sr -> find(idFromReq(sr), service))
             .PUT("/todo", rp, sr -> update(sr.bodyToMono(ToDoItem.class), service))
             .DELETE("/todos", sr -> wipe(service))
             .build();
  }


  private Mono<ServerResponse> add(final Mono<ToDoItem> mono, final ToDoService service) {
    log.info("add");
    return mono.log()
             .flatMap(service::add)
             .flatMap(todo -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(todo))
             .switchIfEmpty(ServerResponse.badRequest().build());
  }

  private Mono<ServerResponse> find(final Mono<Long> mono, final ToDoService service) {
    log.info("find");
    return mono
             .log().flatMap(service::find)
             .flatMap(todo -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(todo))
             .switchIfEmpty(ServerResponse.notFound().build());
  }

  private Mono<ServerResponse> all(final ToDoService service) {
    log.info("all");
    return ServerResponse.ok()
             .contentType(MediaType.APPLICATION_JSON)
             .body(service.all(), ToDoItem.class);
  }

  private Mono<ServerResponse> update(final Mono<ToDoItem> mono, final ToDoService service) {
    log.info("update");
    return mono.flatMap(service::update)
             .flatMap(todo -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(todo))
             .switchIfEmpty(ServerResponse.notFound().build());
  }

  private Mono<ServerResponse> wipe(final ToDoService service) {
    log.info("wipe");
    return service.wipe().log("wipe").flatMap(v -> ServerResponse.ok().build());
  }

  private Mono<Long> idFromReq(final ServerRequest sr) {
    log.info("idFromReq");
    try {
      final Long id = Long.parseLong(sr.pathVariable("id"), 10);
      log.info("idFromReq: {}", id);
      return Mono.just(id);
    } catch (NumberFormatException e) {
      log.error(e.getMessage());
      return Mono.error(e);
    }
  }

  @Bean
  public ConnectionFactoryInitializer connectionFactoryInitializer(
    @Qualifier("connectionFactory") final ConnectionFactory factory
  ) {
    var initializer = new ConnectionFactoryInitializer();
    initializer.setConnectionFactory(factory);
    initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("table.sql")));
    return initializer;
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
             .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
             .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
             .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
             .configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true);
  }

  @Bean
  public ExchangeStrategies exchangeStrategies(final ObjectMapper objectMapper) {
    return ExchangeStrategies.builder()
             .codecs(conf -> {
               conf.defaultCodecs().maxInMemorySize(16 * 1024 * 1204);
               conf.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
             })
             .build();
  }
}
