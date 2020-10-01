package webflux;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ToDoRepository extends ReactiveCrudRepository<ToDoItem, Long> {}
