package jafu;

import org.springframework.data.annotation.Id;

public class ToDoItem {
  @Id Long id;

  Long     ts = System.currentTimeMillis();
  String   title;
  Priority priority;
  Status   status;

  public ToDoItem(Long id, Long ts, String title, Priority priority, Status status) {
    this.id = id;
    this.ts = ts;
    this.title = title;
    this.priority = priority;
    this.status = status;
  }

  public ToDoItem(String title, Priority priority, Status status) {
    this.title = title;
    this.priority = priority;
    this.status = status;
  }

  public ToDoItem() {}


  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Long getTs() { return ts; }
  public void setTs(Long ts) { this.ts = ts; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public Priority getPriority() { return priority; }
  public void setPriority(Priority priority) { this.priority = priority; }

  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }

  public String toString() {
    return "ToDoItem(" +
             "id=" + getId() +
             ", timestamp=" + getTs() +
             ", title=" + getTitle() +
             ", prio=" + getPriority() +
             ", status=" + getStatus() +
             ")";
  }


  enum Priority { HIGH, MEDIUM, LOW }
  enum Status { TODO, DOING, DONE }
}
