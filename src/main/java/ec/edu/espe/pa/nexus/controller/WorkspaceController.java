package ec.edu.espe.pa.nexus.controller;

import ec.edu.espe.pa.nexus.dto.WorkspaceDtos.*;
import ec.edu.espe.pa.nexus.service.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class WorkspaceController {
    private final WorkspaceService service;
    public WorkspaceController(WorkspaceService service) { this.service = service; }

    @GetMapping("/tasks")
    public PageResponse<TaskResponse> tasks(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return service.tasks(pageable);
    }
    @GetMapping("/tasks/search")
    public PageResponse<TaskResponse> searchTasks(@RequestParam String name,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return service.searchTasks(name, pageable);
    }
    @GetMapping("/tasks/{id}") public TaskResponse task(@PathVariable Long id) { return service.task(id); }
    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse created = service.createTask(request);
        return ResponseEntity.created(URI.create("/api/tasks/" + created.id())).body(created);
    }
    @PutMapping("/tasks/{id}")
    public TaskResponse updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return service.updateTask(id, request);
    }
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/projects") public List<ProjectResponse> projects() { return service.projects(); }
    @GetMapping("/projects/{id}") public ProjectResponse project(@PathVariable Long id) { return service.project(id); }
    @PostMapping("/projects")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse created = service.createProject(request);
        return ResponseEntity.created(URI.create("/api/projects/" + created.id())).body(created);
    }
    @PutMapping("/projects/{id}")
    public ProjectResponse updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return service.updateProject(id, request);
    }
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        service.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/documents") public List<DocumentResponse> documents() { return service.documents(); }
    @GetMapping("/documents/{id}") public DocumentResponse document(@PathVariable Long id) { return service.document(id); }
    @PostMapping("/documents")
    public ResponseEntity<DocumentResponse> createDocument(@Valid @RequestBody DocumentRequest request) {
        DocumentResponse created = service.createDocument(request);
        return ResponseEntity.created(URI.create("/api/documents/" + created.id())).body(created);
    }
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        service.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
