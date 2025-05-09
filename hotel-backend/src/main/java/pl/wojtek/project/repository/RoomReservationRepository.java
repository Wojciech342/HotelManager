package pl.wojtek.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wojtek.project.model.RoomReservation;

public interface RoomReservationRepository extends JpaRepository<RoomReservation, Long> {
}
