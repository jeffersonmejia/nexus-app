package ec.edu.espe.pa.nexus.assistant;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
@RequestMapping("/api/assistant/messages")
public class AssistantController {
    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssistantMessageResponse ask(@Valid @RequestBody AssistantMessageRequest request) {
        return assistantService.ask(request.question().trim(), request.context());
    }

    @GetMapping
    public List<AssistantMessageResponse> history() {
        return assistantService.history();
    }
}
