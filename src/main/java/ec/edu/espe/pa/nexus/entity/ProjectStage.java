package ec.edu.espe.pa.nexus.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class ProjectStage {
    private String name;
    private boolean done;
    private String due;

    public ProjectStage() {}

    public ProjectStage(String name, boolean done, String due) {
        this.name = name;
        this.done = done;
        this.due = due;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
    public String getDue() { return due; }
    public void setDue(String due) { this.due = due; }
}
