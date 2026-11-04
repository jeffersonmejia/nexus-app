package ec.edu.espe.pa.nexus.repository;

import ec.edu.espe.pa.nexus.entity.WorkspaceTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<WorkspaceTask, Long> {
    Optional<WorkspaceTask> findByIdAndOwnerId(Long id, Long ownerId);
    boolean existsByOwnerIdAndCode(Long ownerId, String code);
    boolean existsByOwnerIdAndCodeAndIdNot(Long ownerId, String code, Long id);
    Page<WorkspaceTask> findByOwnerId(Long ownerId, Pageable pageable);
    Page<WorkspaceTask> findByOwnerIdAndTitleContainingIgnoreCase(Long ownerId, String title, Pageable pageable);
}
