package ec.edu.espe.pa.nexus.service;

import ec.edu.espe.pa.nexus.dto.WorkspaceDtos.*;
import ec.edu.espe.pa.nexus.entity.*;
import ec.edu.espe.pa.nexus.exception.*;
import ec.edu.espe.pa.nexus.mapper.WorkspaceMapper;
import ec.edu.espe.pa.nexus.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class WorkspaceService {
    private static final Set<String> TASK_SORT_FIELDS = Set.of("id", "code", "title", "course", "date", "time", "priority", "done", "createdAt");
    private final TaskRepository tasks;
    private final ProjectRepository projects;
    private final DocumentRepository documents;
    private final WorkspaceMapper mapper;

    public WorkspaceService(TaskRepository tasks, ProjectRepository projects, DocumentRepository documents, WorkspaceMapper mapper) {
        this.tasks = tasks;
        this.projects = projects;
        this.documents = documents;
        this.mapper = mapper;
    }

    public PageResponse<TaskResponse> tasks(Pageable pageable) {
        Pageable safePage = normalize(pageable);
        return PageResponse.from(tasks.findAll(safePage).map(mapper::toTaskResponse));
    }

    public PageResponse<TaskResponse> searchTasks(String name, Pageable pageable) {
        if (name == null || name.isBlank()) throw new BadRequestException("El parámetro name es obligatorio");
        Pageable safePage = normalize(pageable);
        return PageResponse.from(tasks.findByTitleContainingIgnoreCase(name.trim(), safePage).map(mapper::toTaskResponse));
    }

    public TaskResponse task(Long id) { return mapper.toTaskResponse(requireTask(id)); }

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        WorkspaceTask task = new WorkspaceTask();
        mapper.apply(task, request, request.code() == null || request.code().isBlank()
                ? "TASK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() : request.code().trim());
        ensureTaskRules(request);
        if (tasks.existsByCode(task.getCode())) throw new ConflictException("Ya existe una tarea con el código " + task.getCode());
        return saveTask(task);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        WorkspaceTask task = requireTask(id);
        String code = request.code() == null || request.code().isBlank() ? task.getCode() : request.code().trim();
        if (tasks.existsByCodeAndIdNot(code, id)) throw new ConflictException("Ya existe una tarea con el código " + code);
        mapper.apply(task, request, code);
        ensureTaskRules(request);
        return saveTask(task);
    }

    @Transactional
    public void deleteTask(Long id) { tasks.delete(requireTask(id)); }

    public List<ProjectResponse> projects() { return projects.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream().map(mapper::toProjectResponse).toList(); }
    public ProjectResponse project(Long id) { return mapper.toProjectResponse(requireProject(id)); }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        WorkspaceProject project = new WorkspaceProject();
        mapper.apply(project, request);
        return mapper.toProjectResponse(projects.saveAndFlush(project));
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        WorkspaceProject project = requireProject(id);
        mapper.apply(project, request);
        return mapper.toProjectResponse(projects.saveAndFlush(project));
    }

    @Transactional
    public void deleteProject(Long id) { projects.delete(requireProject(id)); }

    public List<DocumentResponse> documents() { return documents.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream().map(mapper::toDocumentResponse).toList(); }
    public DocumentResponse document(Long id) { return mapper.toDocumentResponse(requireDocument(id)); }

    @Transactional
    public DocumentResponse createDocument(DocumentRequest request) {
        return mapper.toDocumentResponse(documents.saveAndFlush(mapper.toDocument(request)));
    }

    @Transactional
    public void deleteDocument(Long id) { documents.delete(requireDocument(id)); }

    private WorkspaceTask requireTask(Long id) {
        return tasks.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe la tarea con id " + id));
    }
    private WorkspaceProject requireProject(Long id) {
        return projects.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe el proyecto con id " + id));
    }
    private WorkspaceDocument requireDocument(Long id) {
        return documents.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe el documento con id " + id));
    }

    private void ensureTaskRules(TaskRequest request) {
        if (Boolean.FALSE.equals(request.active()) && Boolean.TRUE.equals(request.done()))
            throw new BusinessRuleException("Una tarea inactiva no puede estar completada");
    }

    private TaskResponse saveTask(WorkspaceTask task) {
        try { return mapper.toTaskResponse(tasks.saveAndFlush(task)); }
        catch (DataIntegrityViolationException exception) { throw new ConflictException("El código de tarea ya está registrado"); }
    }

    private Pageable normalize(Pageable pageable) {
        if (pageable.getPageSize() > 100) throw new BadRequestException("El tamaño máximo de página es 100");
        for (Sort.Order order : pageable.getSort()) {
            if (!TASK_SORT_FIELDS.contains(order.getProperty())) throw new BadRequestException("Campo de ordenamiento no permitido: " + order.getProperty());
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
    }
}
