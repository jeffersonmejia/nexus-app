package ec.edu.espe.pa.nexus.repository;

import ec.edu.espe.pa.nexus.entity.WorkspaceDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<WorkspaceDocument, Long> {
    List<WorkspaceDocument> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    Optional<WorkspaceDocument> findByIdAndOwnerId(Long id, Long ownerId);
}
