package pl.wojtek.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wojtek.project.model.RoomReservation;

import java.util.List;
import java.util.Optional;

public interface RoomReservationRepository extends JpaRepository<RoomReservation, Long> {
    Optional<List<RoomReservation>> findByRoomId(Long roomId);
}
