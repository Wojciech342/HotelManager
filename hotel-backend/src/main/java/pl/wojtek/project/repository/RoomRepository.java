package pl.wojtek.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.wojtek.project.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room>, PagingAndSortingRepository<Room, Long> {

}
