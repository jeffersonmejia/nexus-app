package ec.edu.espe.pa.nexus.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class RequestIdInterceptor implements HandlerInterceptor {
    public static final String ATTRIBUTE = RequestIdInterceptor.class.getName() + ".requestId";
    public static final String HEADER = "X-Request-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String incomingRequestId = request.getHeader(HEADER);
        String requestId = isValidRequestId(incomingRequestId)
                ? incomingRequestId
                : UUID.randomUUID().toString();
        request.setAttribute(ATTRIBUTE, requestId);
        response.setHeader(HEADER, requestId);
        return true;
    }

    private boolean isValidRequestId(String requestId) {
        if (requestId == null || !requestId.matches("[0-9a-fA-F]{8}(-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}")) {
            return false;
        }
        try {
            UUID.fromString(requestId);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
