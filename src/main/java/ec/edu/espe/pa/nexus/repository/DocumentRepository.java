package ec.edu.espe.pa.nexus.repository;

import ec.edu.espe.pa.nexus.entity.WorkspaceDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<WorkspaceDocument, Long> {}
