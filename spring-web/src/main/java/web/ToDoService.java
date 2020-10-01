package web;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class ToDoService {

  private final AtomicLong counter = new AtomicLong(0L);
  private final ToDoRepository repo;

  public ToDoService(final ToDoRepository repo) {
    this.repo = repo;
  }

  public ToDoItem add(final ToDoItem todo) {
    todo.setId(counter.getAndIncrement());
    return repo.save(todo);
  }

  public Option<ToDoItem> find(final Long id) {
    return Option.ofOptional(repo.findById(id));
  }

  public List<ToDoItem> all() {
    return List.ofAll(repo.findAll());
  }

  public ToDoItem update(final ToDoItem todo) {
    return repo.save(todo);
  }

  public void wipe() {
    repo.deleteAll();
  }
}
