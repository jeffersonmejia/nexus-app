package ec.edu.espe.pa.nexus.repository;

import ec.edu.espe.pa.nexus.entity.ScheduleEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleEventRepository extends JpaRepository<ScheduleEvent, Long> {
    List<ScheduleEvent> findAllByOwnerIdOrderByDateAscStartTimeAsc(Long ownerId);
    Optional<ScheduleEvent> findByIdAndOwnerId(Long id, Long ownerId);
}
