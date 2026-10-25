package ec.edu.espe.pa.nexus.assistant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "assistant_messages")
public class AssistantMessageEntity {
    @Id private UUID id;
    @Column(nullable = false, length = 2_000) private String question;
    @Column(nullable = false, length = 4_000) private String answer;
    @Column(nullable = false, updatable = false) private Instant createdAt;

    protected AssistantMessageEntity() {}
    public AssistantMessageEntity(String question, String answer) {
        this.id = UUID.randomUUID();
        this.question = question;
        this.answer = answer;
    }
    @PrePersist void onCreate() { if (createdAt == null) createdAt = Instant.now(); }
    public UUID getId() { return id; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public Instant getCreatedAt() { return createdAt; }
}
