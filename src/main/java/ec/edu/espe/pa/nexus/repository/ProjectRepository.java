package ec.edu.espe.pa.nexus.repository;

import ec.edu.espe.pa.nexus.entity.WorkspaceProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<WorkspaceProject, Long> {
    List<WorkspaceProject> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    Optional<WorkspaceProject> findByIdAndOwnerId(Long id, Long ownerId);
}
