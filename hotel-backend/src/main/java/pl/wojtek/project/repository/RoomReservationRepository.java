package pl.wojtek.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.wojtek.project.model.ReservationStatus;
import pl.wojtek.project.model.RoomReservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomReservationRepository extends JpaRepository<RoomReservation, Long>, PagingAndSortingRepository<RoomReservation, Long> {
    Optional<List<RoomReservation>> findByRoomId(Long roomId);
    Page<RoomReservation> findByUserId(Long userId, Pageable pageable);
    @Query("SELECT COUNT(r) > 0 FROM RoomReservation r WHERE r.room.id = :roomId AND r.endDate >= :today")
    boolean existsActiveOrFutureReservation(Long roomId, LocalDate today);
    List<RoomReservation> findByStatus(ReservationStatus status);
}
