package com.example.nexus;

import ec.edu.espe.pa.nexus.NexusApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = NexusApplication.class,
        properties = {
                "jwt.secret=01234567890123456789012345678901",
                "spring.datasource.url=jdbc:h2:mem:nexus;DB_CLOSE_DELAY=-1"
        }
)
class NexusApplicationTests {

    @Test
    void contextLoads() {
    }

}
