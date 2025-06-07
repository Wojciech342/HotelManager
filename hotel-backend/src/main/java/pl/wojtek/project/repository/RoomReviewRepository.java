package pl.wojtek.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wojtek.project.model.RoomReview;

import java.util.List;

public interface RoomReviewRepository extends JpaRepository<RoomReview, Long> {
    List<RoomReview> findByRoomId(Long roomId);
    List<RoomReview> findByUsername(String username);
}