package web;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;

@Data
@Entity
public class ToDoItem {
  @Id
  private Long     id;
  private String   timestamp;
  private String   title;
  private Priority priority;
  private Status   status;

  public ToDoItem() {}

  @PrePersist
  protected void pre() {
    timestamp = String.valueOf(System.currentTimeMillis());
  }

  public ToDoItem status(final Status status) {
    this.status = status;
    return this;
  }
}
