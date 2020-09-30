package web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToDoItem {

  @Id Long id;
  Long ts = System.currentTimeMillis();
  String title;
  Priority priority;
  Status status;

  ToDoItem(final String title, final Priority prio, final Status status) {
    this.title = title;
    this.priority = prio;
    this.status = status;
  }

  ToDoItem status(final Status status) {
    this.status = status;
    return this;
  }


  enum Priority { HIGH, MEDIUM, LOW }
  enum Status { TODO, DOING, DONE }
}
