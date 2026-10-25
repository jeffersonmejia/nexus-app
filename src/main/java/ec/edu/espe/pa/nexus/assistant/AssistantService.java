package ec.edu.espe.pa.nexus.assistant;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AssistantService {
    private final AssistantMessageRepository repository;

    public AssistantService(AssistantMessageRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AssistantMessageResponse ask(String question, String context) {
        String answer;
        if (context != null && !context.isBlank()) {
            String excerpt = context.substring(0, Math.min(context.length(), 500));
            answer = "Recibí tu pregunta sobre el documento. La integración con un modelo de IA aún no está configurada. "
                    + "Este es el inicio del texto disponible para estudiar: " + excerpt;
        } else {
            answer = "Nexus Study recibió tu pregunta. La integración con un modelo de IA aún no está configurada; "
                    + "puedes usarla para organizar tu estudio: " + question;
        }
        AssistantMessageEntity saved = repository.saveAndFlush(new AssistantMessageEntity(question, answer));
        return response(saved);
    }

    public List<AssistantMessageResponse> history() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "createdAt")).stream().map(this::response).toList();
    }

    private AssistantMessageResponse response(AssistantMessageEntity message) {
        return new AssistantMessageResponse(message.getId(), message.getQuestion(), message.getAnswer(), message.getCreatedAt());
    }
}
