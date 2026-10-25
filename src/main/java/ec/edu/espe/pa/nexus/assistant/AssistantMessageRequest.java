package ec.edu.espe.pa.nexus.assistant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AssistantMessageRequest(
        @NotBlank(message = "question must not be blank")
        @Size(max = 2000, message = "question must contain at most 2000 characters")
        String question,
        @Size(max = 100_000, message = "context must contain at most 100000 characters")
        String context) {
}
