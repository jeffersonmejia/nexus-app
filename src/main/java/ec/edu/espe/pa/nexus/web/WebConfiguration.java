package ec.edu.espe.pa.nexus.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private final RequestIdInterceptor requestIdInterceptor;
    private final RequestMonitoringInterceptor requestMonitoringInterceptor;
    private final AuditInterceptor auditInterceptor;

    public WebConfiguration(RequestIdInterceptor requestIdInterceptor,
                            RequestMonitoringInterceptor requestMonitoringInterceptor,
                            AuditInterceptor auditInterceptor) {
        this.requestIdInterceptor = requestIdInterceptor;
        this.requestMonitoringInterceptor = requestMonitoringInterceptor;
        this.auditInterceptor = auditInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestIdInterceptor).addPathPatterns("/api/**").excludePathPatterns("/api/health");
        registry.addInterceptor(requestMonitoringInterceptor).addPathPatterns("/api/**").excludePathPatterns("/api/health");
        registry.addInterceptor(auditInterceptor).addPathPatterns("/api/**").excludePathPatterns("/api/health");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization", RequestIdInterceptor.HEADER)
                .exposedHeaders(RequestIdInterceptor.HEADER);
    }
}
