package ec.edu.espe.pa.nexus.repository;

import ec.edu.espe.pa.nexus.entity.WorkspaceProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<WorkspaceProject, Long> {}
