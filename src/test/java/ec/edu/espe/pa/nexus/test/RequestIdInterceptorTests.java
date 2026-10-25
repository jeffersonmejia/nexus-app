package ec.edu.espe.pa.nexus.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RequestIdInterceptorTests {
    @Autowired MockMvc mockMvc;

    @Test
    void preservesValidRequestId() throws Exception {
        String requestId = "550e8400-e29b-41d4-a716-446655440000";
        mockMvc.perform(get("/api/tasks").header("X-Request-Id", requestId))
                .andExpect(status().isOk()).andExpect(header().string("X-Request-Id", requestId));
    }

    @Test
    void replacesInvalidRequestId() throws Exception {
        mockMvc.perform(get("/api/tasks").header("X-Request-Id", "invalid-id"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Request-Id", org.hamcrest.Matchers.matchesPattern(
                        "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}")));
    }
}
