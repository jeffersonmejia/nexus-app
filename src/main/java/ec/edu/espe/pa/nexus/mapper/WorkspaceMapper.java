package ec.edu.espe.pa.nexus.mapper;

import ec.edu.espe.pa.nexus.dto.WorkspaceDtos.*;
import ec.edu.espe.pa.nexus.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Component
public class WorkspaceMapper {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public void apply(WorkspaceTask task, TaskRequest request, String code) {
        task.setCode(code);
        task.setTitle(request.title().trim());
        task.setCourse(valueOr(request.course(), "Personal"));
        task.setDate(request.date());
        task.setTime(request.time());
        task.setPriority(request.priority());
        task.setDone(request.done());
        task.setActive(request.active() == null || request.active());
        task.setColor(request.color().trim());
        task.setEstimatedHours(request.estimatedHours() == null ? new BigDecimal("1.00") : request.estimatedHours());
    }

    public TaskResponse toTaskResponse(WorkspaceTask task) {
        return new TaskResponse(task.getId(), task.getCode(), task.getTitle(), task.getCourse(), task.getDate().toString(),
                task.getTime().format(TIME_FORMAT), task.getPriority(), task.isDone(), task.isActive(), task.getColor(),
                task.getEstimatedHours(), task.getCreatedAt(), task.getUpdatedAt());
    }

    public void apply(WorkspaceProject project, ProjectRequest request) {
        project.setName(request.name().trim());
        project.setCourse(valueOr(request.course(), "Personal"));
        project.setProgress(request.progress());
        project.setColor(request.color().trim());
        project.setDue(request.due().trim());
        project.setSteps(request.steps().trim());
    }

    public ProjectResponse toProjectResponse(WorkspaceProject project) {
        return new ProjectResponse(project.getId(), project.getName(), project.getCourse(), project.getProgress(),
                project.getColor(), project.getDue(), project.getSteps(), project.getCreatedAt(), project.getUpdatedAt());
    }

    public WorkspaceDocument toDocument(DocumentRequest request) {
        WorkspaceDocument document = new WorkspaceDocument();
        document.setName(request.name().trim());
        document.setSize(request.size().trim());
        document.setPages(request.pages());
        document.setText(request.text() == null ? "" : request.text());
        document.setAdded(request.added().trim());
        return document;
    }

    public DocumentResponse toDocumentResponse(WorkspaceDocument document) {
        return new DocumentResponse(document.getId(), document.getName(), document.getSize(), document.getPages(),
                document.getText(), document.getAdded(), document.getCreatedAt());
    }

    private String valueOr(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
