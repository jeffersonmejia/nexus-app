package ec.edu.espe.pa.nexus.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum TaskPriority {
    HIGH("Alta"), MEDIUM("Media"), LOW("Baja");

    private final String label;

    TaskPriority(String label) { this.label = label; }

    @JsonCreator
    public static TaskPriority fromValue(String value) {
        if (value == null) return null;
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "alta", "high" -> HIGH;
            case "media", "medium" -> MEDIUM;
            case "baja", "low" -> LOW;
            default -> throw new IllegalArgumentException("Priority must be Alta, Media or Baja");
        };
    }

    @JsonValue
    public String label() { return label; }
}
