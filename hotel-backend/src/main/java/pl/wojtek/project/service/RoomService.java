package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.wojtek.project.exception.ResourceNotFoundException;
import pl.wojtek.project.exception.RoomHasActiveReservationsException;
import pl.wojtek.project.exception.RoomNumberAlreadyTakenException;
import pl.wojtek.project.model.Room;
import pl.wojtek.project.model.RoomReservation;
import pl.wojtek.project.model.RoomSpecifications;
import pl.wojtek.project.payload.RoomResponse;
import pl.wojtek.project.repository.RoomRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pl.wojtek.project.repository.RoomReservationRepository;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomReservationRepository roomReservationRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, RoomReservationRepository roomReservationRepository) {
        this.roomRepository = roomRepository;
        this.roomReservationRepository = roomReservationRepository;
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

    public Room createRoomWithImage(Room room, MultipartFile image) throws IOException {
        if(roomRepository.existsByNumber(room.getNumber())) {
            throw new RoomNumberAlreadyTakenException(room.getNumber());
        }
        if (image != null && !image.isEmpty()) {
            String fileName = "room_" + room.getNumber() + "_" + System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path imagePath = Paths.get("uploads/rooms/" + fileName);
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, image.getBytes());
            room.setImageUrl("http://localhost:8080/uploads/rooms/" + fileName);
        }
        room.setAverageRating(0.0);
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));

        // Check for active or future reservations
        if (roomReservationRepository.existsActiveOrFutureReservation(id, LocalDate.now())) {
            throw new RoomHasActiveReservationsException("Cannot delete room with active or upcoming reservations.");
        }

        // Find all past reservations for this room and set room to null
        List<RoomReservation> pastReservations = roomReservationRepository.findByRoomId(id)
                .orElse(List.of());

        for (RoomReservation reservation : pastReservations) {
            reservation.setImageUrl(room.getImageUrl()); // Keep the image URL for reference
            reservation.setRoom(null); // Disconnect from the room
        }

        // Save all updated reservations
        if (!pastReservations.isEmpty()) {
            roomReservationRepository.saveAll(pastReservations);
        }

        // Now delete the room
        roomRepository.deleteById(id);
    }

    public void deleteAllRooms() {
        roomRepository.deleteAll();
    }

    public RoomResponse getFilteredRooms(List<String> types, Double minRating, Double minPrice,
                                         Double maxPrice, Integer pageNumber, Integer pageSize,
                                         String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
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

        Page<Room> pageRooms = roomRepository.findAll(spec, pageDetails);
        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setContent(pageRooms.getContent());
        roomResponse.setPageNumber(pageRooms.getNumber());
        roomResponse.setPageSize(pageRooms.getSize());
        roomResponse.setTotalElements(pageRooms.getTotalElements());
        roomResponse.setTotalPages(pageRooms.getTotalPages());
        roomResponse.setLastPage(pageRooms.isLast());
        return roomResponse;
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
