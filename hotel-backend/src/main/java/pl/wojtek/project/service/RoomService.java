package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.wojtek.project.exception.ResourceNotFoundException;
import pl.wojtek.project.exception.RoomHasActiveReservationsException;
import pl.wojtek.project.exception.ResourceAlreadyExistsException;
import pl.wojtek.project.model.ReservationStatus;
import pl.wojtek.project.model.Room;
import pl.wojtek.project.model.RoomReservation;
import pl.wojtek.project.model.RoomSpecifications;
import pl.wojtek.project.message.response.RoomResponse;
import pl.wojtek.project.repository.RoomRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pl.wojtek.project.repository.RoomReservationRepository;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomReservationRepository roomReservationRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Autowired
    public RoomService(RoomRepository roomRepository, RoomReservationRepository roomReservationRepository) {
        this.roomRepository = roomRepository;
        this.roomReservationRepository = roomReservationRepository;
    }

    @Transactional
    public Room createRoom(Room room) {
        validateRoomNumber(room.getNumber());

        room.setAverageRating(0.0);

        return roomRepository.save(room);
    }

    @Transactional
    public Room createRoomWithImage(Room room, MultipartFile image) throws IOException {
        validateRoomNumber(room.getNumber());

        if (image != null && !image.isEmpty()) {
            room.setImageUrl(storeRoomImage(room.getNumber(), image));
        }

        room.setAverageRating(0.0);
        return roomRepository.save(room);
    }

    public RoomResponse getFilteredRooms(List<String> types, Double minRating, Double minPrice,
                                         Double maxPrice, Integer pageNumber, Integer pageSize,
                                         String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Specification<Room> spec = buildRoomSpecification(types, minRating, minPrice, maxPrice);
        Page<Room> pageRooms = roomRepository.findAll(spec, pageDetails);

        return createRoomResponse(pageRooms);
    }

    public Room getRoomById(long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
    }

    @Transactional
    public Room updateRoom(Long id, Room room) {
        Room roomFromDB = getRoomById(id);

        if (!roomFromDB.getNumber().equals(room.getNumber())) {
            if (roomRepository.existsByNumber(room.getNumber())) {
                throw new ResourceAlreadyExistsException("Room", "number", room.getNumber().toString());
            }
            roomFromDB.setNumber(room.getNumber());
        }

        roomFromDB.setCapacity(room.getCapacity());
        roomFromDB.setPricePerNight(room.getPricePerNight());
        roomFromDB.setType(room.getType());

        return roomRepository.save(roomFromDB);
    }

    @Transactional
    public Room updateRoomImage(Long roomId, MultipartFile image) throws IOException {
        Room room = getRoomById(roomId);

        if (image != null && !image.isEmpty()) {
            room.setImageUrl(storeRoomImage(room.getNumber(), image));
        }

        return roomRepository.save(room);
    }

    @Transactional
    public List<Room> updateAllRoomsImage(MultipartFile image) throws IOException {
        List<Room> rooms = roomRepository.findAll();
        for (Room room : rooms) {
            if (image != null && !image.isEmpty()) {
                room.setImageUrl(storeRoomImage(room.getNumber(), image));
            }
        }
        return rooms;
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = getRoomById(id);

        if (roomReservationRepository.existsActiveOrFutureReservation(id, LocalDate.now())) {
            throw new RoomHasActiveReservationsException("Cannot delete room with active or upcoming reservations.");
        }

        handleExistingRoomReservations(room.getId(), room.getImageUrl());

        roomRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllRooms() {
        for (Room room : roomRepository.findAll()) {
            if (roomReservationRepository.existsActiveOrFutureReservation(room.getId(), LocalDate.now())) {
                throw new RoomHasActiveReservationsException("Cannot delete room with active or upcoming reservations.");
            }

            handleExistingRoomReservations(room.getId(), room.getImageUrl());
        }
        roomRepository.deleteAll();
    }

    private String storeRoomImage(Integer roomNumber, MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";

        String fileName = "room_" + roomNumber + "_" + UUID.randomUUID() + extension;

        Path imagePath = Paths.get(uploadDir, "rooms", fileName);
        Files.createDirectories(imagePath.getParent());

        Files.write(imagePath, image.getBytes());

        return baseUrl + "/uploads/rooms/" + fileName;
    }

    private void handleExistingRoomReservations(Long roomId, String imageUrl) {
        List<RoomReservation> reservations = roomReservationRepository.findByRoomId(roomId)
                .orElse(List.of());

        for (RoomReservation reservation : reservations) {
            reservation.setImageUrl(imageUrl);
            reservation.setRoom(null);

            if(reservation.getStatus() == ReservationStatus.PENDING) {
                reservation.setStatus(ReservationStatus.REJECTED);
            }
        }

        if (!reservations.isEmpty()) {
            roomReservationRepository.saveAll(reservations);
        }
    }

    private void validateRoomNumber(Integer roomNumber) {
        if (roomRepository.existsByNumber(roomNumber)) {
            throw new ResourceAlreadyExistsException("Room", "number", roomNumber.toString());
        }
    }

    private Specification<Room> buildRoomSpecification(
            List<String> types, Double minRating, Double minPrice, Double maxPrice) {
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

        return spec;
    }

    private RoomResponse createRoomResponse(Page<Room> pageRooms) {
        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setContent(pageRooms.getContent());
        roomResponse.setPageNumber(pageRooms.getNumber());
        roomResponse.setPageSize(pageRooms.getSize());
        roomResponse.setTotalElements(pageRooms.getTotalElements());
        roomResponse.setTotalPages(pageRooms.getTotalPages());
        roomResponse.setLastPage(pageRooms.isLast());
        return roomResponse;
    }
}
