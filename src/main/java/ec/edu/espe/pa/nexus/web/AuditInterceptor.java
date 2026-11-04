package ec.edu.espe.pa.nexus.web;

import ec.edu.espe.pa.nexus.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuditInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AuditInterceptor.class);

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) {
        if (!isAuditable(request) || response.getStatus() >= 500) return;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String actor = authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser user
                ? String.valueOf(user.id()) : "anonymous";
        log.info("AUDIT actor={} action={} method={} path={} status={}", actor,
                action(request), request.getMethod(), request.getRequestURI(), response.getStatus());
    }

    private boolean isAuditable(HttpServletRequest request) {
        return !"GET".equalsIgnoreCase(request.getMethod())
                && !"OPTIONS".equalsIgnoreCase(request.getMethod())
                && request.getRequestURI().startsWith("/api/");
    }

    private String action(HttpServletRequest request) {
        return switch (request.getMethod().toUpperCase()) {
            case "POST" -> "CREATE";
            case "PUT", "PATCH" -> "UPDATE";
            case "DELETE" -> "DELETE";
            default -> request.getMethod().toUpperCase();
        };
    }
}
