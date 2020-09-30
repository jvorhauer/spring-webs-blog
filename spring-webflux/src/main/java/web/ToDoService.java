package web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ToDoService {

  private final @NonNull ToDoRepository repo;

  @Transactional
  public Mono<ToDoItem> add(final ToDoItem todo) {
    log.info("add: todo: " + todo);
    return repo.save(todo);
  }

  @Transactional(readOnly = true)
  public Mono<ToDoItem> find(final Long id) {
    return repo.findById(id);
  }

  @Transactional(readOnly = true)
  public Flux<ToDoItem> all() {
    return repo.findAll();
  }

  @Transactional
  public Mono<ToDoItem> update(final ToDoItem todo) {
    return repo.save(todo);
  }

  @Transactional
  public Mono<Void> wipe() {
    return repo.deleteAll();
  }
}
