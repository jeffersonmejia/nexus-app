package ec.edu.espe.pa.nexus.service;

import ec.edu.espe.pa.nexus.dto.AuthDtos.*;
import ec.edu.espe.pa.nexus.entity.UserAccount;
import ec.edu.espe.pa.nexus.exception.ConflictException;
import ec.edu.espe.pa.nexus.exception.UnauthorizedException;
import ec.edu.espe.pa.nexus.repository.UserRepository;
import ec.edu.espe.pa.nexus.security.CurrentUser;
import ec.edu.espe.pa.nexus.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CurrentUser currentUser;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService,
                       CurrentUser currentUser) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.currentUser = currentUser;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalize(request.email());
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Ya existe una cuenta con el correo " + email);
        }
        UserAccount user = new UserAccount();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        UserAccount saved = users.saveAndFlush(user);
        return new AuthResponse(jwtService.generate(saved), toResponse(saved));
    }

    public AuthResponse login(LoginRequest request) {
        UserAccount user = users.findByEmailIgnoreCase(normalize(request.email()))
                .orElseThrow(() -> new UnauthorizedException("Correo o contraseña incorrectos"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Correo o contraseña incorrectos");
        }
        return new AuthResponse(jwtService.generate(user), toResponse(user));
    }

    public UserResponse me() {
        UserAccount user = users.findById(currentUser.id())
                .orElseThrow(() -> new UnauthorizedException("La cuenta ya no existe"));
        return toResponse(user);
    }

    private String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private UserResponse toResponse(UserAccount user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
