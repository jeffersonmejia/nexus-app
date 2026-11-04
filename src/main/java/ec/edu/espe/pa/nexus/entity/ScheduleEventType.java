package ec.edu.espe.pa.nexus.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum ScheduleEventType {
    CLASS("Clase"),
    EXAM("Examen"),
    DELIVERY("Entrega"),
    REMINDER("Recordatorio");

    private final String label;

    ScheduleEventType(String label) { this.label = label; }

    @JsonCreator
    public static ScheduleEventType fromValue(String value) {
        if (value == null) return null;
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "clase", "class" -> CLASS;
            case "examen", "exam" -> EXAM;
            case "entrega", "delivery" -> DELIVERY;
            case "recordatorio", "reminder" -> REMINDER;
            default -> throw new IllegalArgumentException("El tipo debe ser Clase, Examen, Entrega o Recordatorio");
        };
    }

    @JsonValue
    public String label() { return label; }
}
