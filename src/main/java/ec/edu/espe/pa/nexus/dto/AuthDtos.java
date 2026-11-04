package ec.edu.espe.pa.nexus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {}

    public record RegisterRequest(
            @NotBlank(message = "El nombre es obligatorio") @Size(max = 120) String name,
            @NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no es válido")
            @Size(max = 190) String email,
            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres") String password) {}

    public record LoginRequest(
            @NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no es válido") String email,
            @NotBlank(message = "La contraseña es obligatoria") String password) {}

    public record UserResponse(Long id, String name, String email) {}

    public record AuthResponse(String token, UserResponse user) {}
}
