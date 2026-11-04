package ec.edu.espe.pa.nexus.assistant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssistantMessageRepository extends JpaRepository<AssistantMessageEntity, UUID> {
    List<AssistantMessageEntity> findAllByOwnerIdOrderByCreatedAtAsc(Long ownerId);
}
