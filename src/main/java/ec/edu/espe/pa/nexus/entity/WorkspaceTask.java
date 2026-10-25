package ec.edu.espe.pa.nexus.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "study_tasks", uniqueConstraints = @UniqueConstraint(name = "uk_study_tasks_code", columnNames = "code"))
public class WorkspaceTask {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 40)
    private String code;
    @Column(nullable = false, length = 160)
    private String title;
    @Column(nullable = false, length = 80)
    private String course;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private LocalTime time;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 12)
    private TaskPriority priority;
    @Column(nullable = false)
    private boolean done;
    @Column(nullable = false)
    private boolean active;
    @Column(nullable = false, length = 24)
    private String color;
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal estimatedHours;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    private Instant updatedAt;

    public WorkspaceTask() {}

    @PrePersist void onCreate() { createdAt = Instant.now(); }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public BigDecimal getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(BigDecimal estimatedHours) { this.estimatedHours = estimatedHours; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
