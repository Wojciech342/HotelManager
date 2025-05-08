package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.wojtek.project.exception.ResourceNotFoundException;
import pl.wojtek.project.model.Room;
import pl.wojtek.project.model.RoomSpecifications;
import pl.wojtek.project.repository.RoomRepository;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return rooms;
    }

    public Room getRoomById(long id) {
        Room room = roomRepository.findById(id).orElse(null);
        return room;
    }

    public Room createRoom(Room room) {
        Room createdRoom = roomRepository.save(room);
        return createdRoom;
    }

    public void deleteRoom(Long id) {
        roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));

        roomRepository.deleteById(id);
    }

    public void deleteAllRooms() {
        roomRepository.deleteAll();
    }

    public List<Room> getFilteredRooms(String type, Double minRating, Double minPrice, Double maxPrice) {
        Specification<Room> spec = Specification.where(null);

        if (type != null) {
            spec = spec.and(RoomSpecifications.hasType(type));
        }
        if (minRating != null) {
            spec = spec.and(RoomSpecifications.hasMinRating(minRating));
        }
        if (minPrice != null && maxPrice != null) {
            spec = spec.and(RoomSpecifications.hasPriceBetween(minPrice, maxPrice));
        }

        return roomRepository.findAll(spec);
    }
}
