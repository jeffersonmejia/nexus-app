package ec.edu.espe.pa.nexus.assistant;

import ec.edu.espe.pa.nexus.entity.UserAccount;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "assistant_messages")
public class AssistantMessageEntity {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserAccount owner;
    @Column(nullable = false, length = 4_000) private String question;
    @Column(nullable = false, length = 16_000) private String answer;
    @Column(nullable = false, updatable = false) private Instant createdAt;

    protected AssistantMessageEntity() {}
    public AssistantMessageEntity(UserAccount owner, String question, String answer) {
        this.id = UUID.randomUUID();
        this.owner = owner;
        this.question = question;
        this.answer = answer;
    }
    @PrePersist void onCreate() { if (createdAt == null) createdAt = Instant.now(); }
    public UUID getId() { return id; }
    public UserAccount getOwner() { return owner; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public Instant getCreatedAt() { return createdAt; }
}
