package web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ToDoController {

  private static final Logger logger = LoggerFactory.getLogger(ToDoController.class);
  private final ToDoService service;

  public ToDoController(final ToDoService service) {
    this.service = service;
  }

  @PostMapping(value = "/todos", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ToDoItem add(@RequestBody final ToDoItem todo) {
    logger.info("add: {}", todo);
    return service.add(todo);
  }

  @GetMapping(value = "/todos", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ToDoItem> all() {
    logger.info("all");
    return service.all().toJavaList();
  }

  @GetMapping(value = "/todos/todo", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ToDoItem> todo() {
    logger.info("todo");
    return service.all().filter(todo -> todo.getStatus() == Status.TODO).toJavaList();
  }

  @GetMapping(value = "/todos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ToDoItem> find(@PathVariable("id") final Long id) {
    logger.info("find: {}", id);
    return service.find(id)
             .map(ResponseEntity::ok)
             .getOrElse(ResponseEntity.notFound().build());
  }

  @PutMapping(value = "/todos/{id}/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ToDoItem> update(@PathVariable("id") final Long id, @PathVariable("status") final Status status) {
    logger.info("update: {} -> {}", id, status);
    return service.find(id)
             .filter(todo -> status != null)
             .map(todo -> service.update(todo, status))
             .map(ResponseEntity::ok)
             .getOrElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping(value = "/todos")
  public ResponseEntity<Void> wipe() {
    logger.info("wipe");
    service.wipe();
    return ResponseEntity.ok().build();
  }
}
