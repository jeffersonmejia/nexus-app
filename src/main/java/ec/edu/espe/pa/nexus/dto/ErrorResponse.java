package ec.edu.espe.pa.nexus.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(Instant timestamp, int status, String error, String message, String path,
                            List<String> errors) {
}
