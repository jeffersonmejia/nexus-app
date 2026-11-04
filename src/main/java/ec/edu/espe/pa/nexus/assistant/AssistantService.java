package ec.edu.espe.pa.nexus.assistant;

import ec.edu.espe.pa.nexus.entity.UserAccount;
import ec.edu.espe.pa.nexus.repository.UserRepository;
import ec.edu.espe.pa.nexus.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssistantService {
    private final AssistantMessageRepository repository;
    private final OpenRouterClient openRouterClient;
    private final UserRepository users;
    private final CurrentUser currentUser;

    public AssistantService(AssistantMessageRepository repository, OpenRouterClient openRouterClient,
                            UserRepository users, CurrentUser currentUser) {
        this.repository = repository;
        this.openRouterClient = openRouterClient;
        this.users = users;
        this.currentUser = currentUser;
    }

    @Transactional
    public AssistantMessageResponse ask(String question, String context) {
        String answer;
        try {
            answer = openRouterClient.answer(question, context);
        } catch (IllegalStateException exception) {
            answer = "No pude contactar al asistente de IA en este momento. " + exception.getMessage()
                    + "\n\nMientras tanto puedo ayudarte a organizar tu estudio: divide la pregunta en pasos, "
                    + "revisa tus apuntes y crea un plan con bloques de 25 minutos.";
        }

        String safeQuestion = question.length() > 4_000 ? question.substring(0, 4_000) : question;
        String safeAnswer = answer.length() > 16_000 ? answer.substring(0, 16_000) : answer;
        UserAccount owner = users.getReferenceById(currentUser.id());
        AssistantMessageEntity saved = repository.saveAndFlush(new AssistantMessageEntity(owner, safeQuestion, safeAnswer));
        return response(saved);
    }

    @Transactional(readOnly = true)
    public List<AssistantMessageResponse> history() {
        return repository.findAllByOwnerIdOrderByCreatedAtAsc(currentUser.id()).stream().map(this::response).toList();
    }

    private AssistantMessageResponse response(AssistantMessageEntity message) {
        return new AssistantMessageResponse(message.getId(), message.getQuestion(), message.getAnswer(), message.getCreatedAt());
    }
}
