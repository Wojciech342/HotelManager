package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.wojtek.project.exception.ResourceNotFoundException;
import pl.wojtek.project.model.Room;
import pl.wojtek.project.model.RoomSpecifications;
import pl.wojtek.project.repository.RoomRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
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

    public List<Room> getFilteredRooms(List<String> types, Double minRating, Double minPrice, Double maxPrice) {
        Specification<Room> spec = Specification.where(null);

        if (types != null && !types.isEmpty()) {
            spec = spec.and(RoomSpecifications.hasTypes(types));
        }
        if (minRating != null) {
            spec = spec.and(RoomSpecifications.hasMinRating(minRating));
        }
        if (maxPrice != null) {
            spec = spec.and(RoomSpecifications.hasPriceLessThan(maxPrice));
        }
        if (minPrice != null) {
            spec = spec.and(RoomSpecifications.hasPriceGreaterThan(minPrice));
        }
        return roomRepository.findAll(spec);
    }

    public Room updateRoomImage(Long roomId, MultipartFile image) throws IOException {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        String fileName = "room_" + roomId + "_" + System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path imagePath = Paths.get("uploads/rooms/" + fileName);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, image.getBytes());

        room.setImageUrl("http://localhost:8080/uploads/rooms/" + fileName);
        return roomRepository.save(room);
    }

    public List<Room> updateAllRoomsImage(MultipartFile image) throws IOException {
        List<Room> rooms = roomRepository.findAll();
        for (Room room : rooms) {
            updateRoomImage(room.getId(), image);
        }
        return rooms;
    }
}
