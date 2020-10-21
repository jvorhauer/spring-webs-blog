package jafu;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// see: https://spring.io/guides/gs/accessing-data-r2dbc/
class ToDoRepository {

  private final R2dbcEntityOperations ops;

  public ToDoRepository(final R2dbcEntityOperations ops) {
    this.ops = ops;
  }

  Flux<ToDoItem> all() {
    return ops.select(Query.empty(), ToDoItem.class);
  }

  Mono<ToDoItem> save(final ToDoItem todo) {
    return ops.insert(todo);
  }

  Mono<ToDoItem> find(final Long id) {
    return ops.select(ToDoItem.class).matching(query(where("id").is(id))).one();
  }

  Mono<Integer> wipe() {
    return ops.delete(ToDoItem.class).all();
  }
}
