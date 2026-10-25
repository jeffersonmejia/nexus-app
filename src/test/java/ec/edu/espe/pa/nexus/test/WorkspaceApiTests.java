package ec.edu.espe.pa.nexus.test;

import ec.edu.espe.pa.nexus.repository.TaskRepository;
import ec.edu.espe.pa.nexus.repository.ProjectRepository;
import ec.edu.espe.pa.nexus.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WorkspaceApiTests {
    @Autowired MockMvc mockMvc;
    @Autowired TaskRepository tasks;
    @Autowired ProjectRepository projects;
    @Autowired DocumentRepository documents;

    @BeforeEach void cleanDatabase() { tasks.deleteAll(); projects.deleteAll(); documents.deleteAll(); }

    @Test
    void createsReadsUpdatesAndDeletesProjectsAndDocuments() throws Exception {
        String project = mockMvc.perform(post("/api/projects").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Proyecto API\",\"course\":\"Avanzada\",\"progress\":25,\"color\":\"blue\",\"due\":\"2026-11-15\",\"steps\":\"1 de 4 etapas\"}"))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        long projectId = ((Number) com.jayway.jsonpath.JsonPath.read(project, "$.id")).longValue();
        mockMvc.perform(get("/api/projects/{id}", projectId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Proyecto API"));
        mockMvc.perform(put("/api/projects/{id}", projectId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Proyecto actualizado\",\"course\":\"Avanzada\",\"progress\":50,\"color\":\"blue\",\"due\":\"2026-11-15\",\"steps\":\"2 de 4 etapas\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.progress").value(50));
        mockMvc.perform(delete("/api/projects/{id}", projectId)).andExpect(status().isNoContent());

        String document = mockMvc.perform(post("/api/documents").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"apuntes.pdf\",\"size\":\"0.5\",\"pages\":2,\"text\":\"Texto de prueba\",\"added\":\"25 oct 2026\"}"))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        long documentId = ((Number) com.jayway.jsonpath.JsonPath.read(document, "$.id")).longValue();
        mockMvc.perform(get("/api/documents/{id}", documentId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Texto de prueba"));
        mockMvc.perform(delete("/api/documents/{id}", documentId)).andExpect(status().isNoContent());
    }

    @Test
    void createsTaskWithLocationThenSupportsPagedListAndSearch() throws Exception {
        String created = mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(validTask("LAB-001")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith("/api/tasks/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        Number idValue = com.jayway.jsonpath.JsonPath.read(created, "$.id");
        long id = idValue.longValue();

        mockMvc.perform(get("/api/tasks/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("LAB-001"));
        mockMvc.perform(get("/api/tasks").param("page", "0").param("size", "5").param("sort", "title,asc"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.page.number").value(0)).andExpect(jsonPath("$.page.totalElements").value(1));
        mockMvc.perform(get("/api/tasks/search").param("name", "informe"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].title").value("Preparar informe"));
    }

    @Test
    void mapsValidationConflictBusinessRuleMissingResourceAndUnsupportedMethod() throws Exception {
        mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(org.hamcrest.Matchers.greaterThan(0)));
        mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(validTask("DUPLICATE")))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(validTask("DUPLICATE")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.errors").isArray());
        mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(
                        "{\"title\":\"Tarea inválida\",\"course\":\"Personal\",\"date\":\"2026-11-01\",\"time\":\"18:00\",\"priority\":\"Alta\",\"done\":true,\"active\":false,\"color\":\"blue\"}"))
                .andExpect(status().isUnprocessableEntity());
        mockMvc.perform(get("/api/tasks/999999")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        mockMvc.perform(get("/api/tasks/no-numerico")).andExpect(status().isBadRequest());
        mockMvc.perform(patch("/api/tasks/1")).andExpect(status().isMethodNotAllowed());
    }

    private String validTask(String code) {
        return ("{\"code\":\"%s\",\"title\":\"Preparar informe\",\"course\":\"Programación avanzada\","
                + "\"date\":\"2026-11-01\",\"time\":\"18:00\",\"priority\":\"Alta\",\"done\":false,"
                + "\"active\":true,\"color\":\"violet\",\"estimatedHours\":2.50}").formatted(code);
    }
}
