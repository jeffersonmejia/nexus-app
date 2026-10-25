package ec.edu.espe.pa.nexus.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "study_projects")
public class WorkspaceProject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 160)
    private String name;
    @Column(nullable = false, length = 80)
    private String course;
    @Column(nullable = false)
    private int progress;
    @Column(nullable = false, length = 24)
    private String color;
    @Column(nullable = false, length = 40)
    private String due;
    @Column(nullable = false, length = 160)
    private String steps;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    private Instant updatedAt;

    public WorkspaceProject() {}
    @PrePersist void onCreate() { createdAt = Instant.now(); }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getDue() { return due; }
    public void setDue(String due) { this.due = due; }
    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
