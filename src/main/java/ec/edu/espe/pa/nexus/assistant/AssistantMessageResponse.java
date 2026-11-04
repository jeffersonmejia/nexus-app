package ec.edu.espe.pa.nexus.assistant;

import java.time.Instant;
import java.util.UUID;

public record AssistantMessageResponse(UUID id, String question, String answer, Instant createdAt) {
}
