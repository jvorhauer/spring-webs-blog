package jafu;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

class ToDoHandler {

  private final ToDoRepository repo;

  ToDoHandler(final ToDoRepository repo) {
    this.repo = repo;
  }


  Mono<ServerResponse> all(final ServerRequest req) {
    return ok().body(repo.all(), ToDoItem.class);
  }

  Mono<ServerResponse> add(final ServerRequest req) {
    return req.bodyToMono(ToDoItem.class)
             .flatMap(repo::save)
             .flatMap(todo -> ok().bodyValue(todo));
  }

  Mono<ServerResponse> find(final ServerRequest req) {
    return Mono.just(req.pathVariable("id"))
             .map(Long::parseLong)
             .flatMap(repo::find)
             .flatMap(todo -> ok().bodyValue(todo));

  }
}
