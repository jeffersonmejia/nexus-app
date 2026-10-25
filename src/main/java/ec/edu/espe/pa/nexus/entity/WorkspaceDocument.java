package ec.edu.espe.pa.nexus.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "study_documents")
public class WorkspaceDocument {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 255)
    private String name;
    @Column(nullable = false, length = 20)
    private String size;
    @Column(nullable = false)
    private int pages;
    @Column(nullable = false, length = 100_000)
    private String text;
    @Column(nullable = false, length = 40)
    private String added;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public WorkspaceDocument() {}
    @PrePersist void onCreate() { createdAt = Instant.now(); }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getAdded() { return added; }
    public void setAdded(String added) { this.added = added; }
    public Instant getCreatedAt() { return createdAt; }
}
