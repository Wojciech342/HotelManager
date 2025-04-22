package pl.wojtek.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wojtek.project.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

}
