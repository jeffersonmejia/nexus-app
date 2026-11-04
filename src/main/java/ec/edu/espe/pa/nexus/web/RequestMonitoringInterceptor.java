package ec.edu.espe.pa.nexus.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestMonitoringInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RequestMonitoringInterceptor.class);
    private static final String START = RequestMonitoringInterceptor.class.getName() + ".start";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START, System.nanoTime());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) {
        Object start = request.getAttribute(START);
        long elapsedMs = start instanceof Long value ? (System.nanoTime() - value) / 1_000_000 : -1;
        if (exception == null) {
            log.info("HTTP {} {} -> {} ({} ms)", request.getMethod(), request.getRequestURI(),
                    response.getStatus(), elapsedMs);
        } else {
            log.warn("HTTP {} {} -> {} ({} ms): {}", request.getMethod(), request.getRequestURI(),
                    response.getStatus(), elapsedMs, exception.getMessage());
        }
    }
}
