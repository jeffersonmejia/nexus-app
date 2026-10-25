package ec.edu.espe.pa.nexus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ec.edu.espe.pa.nexus.entity.TaskPriority;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class WorkspaceDtos {
    private WorkspaceDtos() {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TaskRequest(
            @Size(max = 40, message = "El código admite máximo 40 caracteres") String code,
            @NotBlank(message = "El título es obligatorio") @Size(max = 160) String title,
            @Size(max = 80) String course,
            @NotNull(message = "La fecha es obligatoria") @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @NotNull(message = "La hora es obligatoria") @JsonFormat(pattern = "HH:mm") LocalTime time,
            @NotNull(message = "La prioridad es obligatoria") TaskPriority priority,
            @NotNull(message = "El estado completado es obligatorio") Boolean done,
            Boolean active,
            @NotBlank @Size(max = 24) String color,
            @DecimalMin(value = "0.25", message = "Las horas estimadas deben ser al menos 0.25")
            @Digits(integer = 6, fraction = 2) BigDecimal estimatedHours) {}

    public record TaskResponse(Long id, String code, String title, String course, String date,
                               String time, TaskPriority priority, boolean done, boolean active,
                               String color, BigDecimal estimatedHours, Instant createdAt, Instant updatedAt) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ProjectRequest(
            @NotBlank @Size(max = 160) String name,
            @Size(max = 80) String course,
            @NotNull @Min(0) @Max(100) Integer progress,
            @NotBlank @Size(max = 24) String color,
            @NotBlank @Size(max = 40) String due,
            @NotBlank @Size(max = 160) String steps) {}

    public record ProjectResponse(Long id, String name, String course, int progress,
                                  String color, String due, String steps, Instant createdAt, Instant updatedAt) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DocumentRequest(
            @NotBlank @Size(max = 255) String name,
            @NotBlank @Size(max = 20) String size,
            @Min(1) @Max(8) int pages,
            @Size(max = 100_000) String text,
            @NotBlank @Size(max = 40) String added) {}

    public record DocumentResponse(Long id, String name, String size, int pages,
                                   String text, String added, Instant createdAt) {}

    public record PageMetadata(int number, int size, long totalElements, int totalPages) {}
    public record PageResponse<T>(List<T> content, PageMetadata page) {
        public static <T> PageResponse<T> from(Page<T> result) {
            return new PageResponse<>(result.getContent(), new PageMetadata(result.getNumber(), result.getSize(),
                    result.getTotalElements(), result.getTotalPages()));
        }
    }
}
