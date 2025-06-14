package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.wojtek.project.exception.ResourceNotFoundException;
import pl.wojtek.project.model.ReservationStatus;
import pl.wojtek.project.model.Room;
import pl.wojtek.project.model.RoomReservation;
import pl.wojtek.project.model.User;
import pl.wojtek.project.payload.RoomReservationResponse;
import pl.wojtek.project.payload.RoomResponse;
import pl.wojtek.project.repository.RoomRepository;
import pl.wojtek.project.repository.RoomReservationRepository;
import pl.wojtek.project.repository.UserRepository;

import java.util.List;

@Service
public class RoomReservationService {

    private final RoomReservationRepository roomReservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoomReservationService(RoomReservationRepository roomReservationRepository,
                                  RoomRepository roomRepository,
                                  UserRepository userRepository) {
        this.roomReservationRepository = roomReservationRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public RoomReservation createRoomReservation(String username, Long roomId, RoomReservation roomReservation) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));

        if (hasOverlappingReservation(room, roomReservation)) {
            throw new IllegalArgumentException("Reservation dates overlap with an existing reservation");
        }

        roomReservation.setUser(user);
        roomReservation.setRoom(room);
        roomReservation.setImageUrl(room.getImageUrl());
        roomReservation.setStatus(ReservationStatus.PENDING);

        return roomReservationRepository.save(roomReservation);
    }

    private boolean hasOverlappingReservation(Room room, RoomReservation newReservation) {
        return roomReservationRepository.findByRoomId(room.getId())
                .orElse(List.of())
                .stream()
                .filter(res -> res.getStatus() != ReservationStatus.CANCELLED &&
                        res.getStatus() != ReservationStatus.REJECTED)
                .anyMatch(res ->
                        newReservation.getStartDate().isBefore(res.getEndDate()) &&
                                newReservation.getEndDate().isAfter(res.getStartDate())
                );
    }

    public RoomReservationResponse getRoomReservations(Integer pageNumber, Integer pageSize,
                                                       String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<RoomReservation> pageRoomReservations = roomReservationRepository.findAll(pageDetails);
        RoomReservationResponse roomReservationResponse = new RoomReservationResponse();
        roomReservationResponse.setContent(pageRoomReservations.getContent());
        roomReservationResponse.setPageNumber(pageRoomReservations.getNumber());
        roomReservationResponse.setPageSize(pageRoomReservations.getSize());
        roomReservationResponse.setTotalElements(pageRoomReservations.getTotalElements());
        roomReservationResponse.setTotalPages(pageRoomReservations.getTotalPages());
        roomReservationResponse.setLastPage(pageRoomReservations.isLast());
        return roomReservationResponse;
    }

    public RoomReservation getRoomReservationById(Long id) {
        RoomReservation roomReservation = roomReservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReservation", "id", id));
        return roomReservation;
    }

    public RoomReservation updateRoomReservation(Long id, RoomReservation roomReservation) {
        RoomReservation roomReservationFromDB = roomReservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReservation", "id", id));

        if (roomReservation.getStartDate() != null) {
            roomReservationFromDB.setStartDate(roomReservation.getStartDate());
        }
        if (roomReservation.getEndDate() != null) {
            roomReservationFromDB.setEndDate(roomReservation.getEndDate());
        }
        if (roomReservation.getPrice() != 0) {
            roomReservationFromDB.setPrice(roomReservation.getPrice());
        }
        if (roomReservation.getStatus() != null) {
            roomReservationFromDB.setStatus(roomReservation.getStatus());
        }

        RoomReservation updatedRoomReservation = roomReservationRepository.save(roomReservationFromDB);
        return updatedRoomReservation;
    }

    public void deleteRoomReservationById(Long id) {
        roomReservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReservation", "id", id));

        roomReservationRepository.deleteById(id);
    }

    public List<RoomReservation> getRoomReservationsByRoomId(Long roomId) {
        List<RoomReservation> roomReservations = roomReservationRepository
                .findByRoomId(roomId).orElseThrow(() -> new ResourceNotFoundException("RoomReservation", "roomId", roomId));

        return roomReservations;
    }

    public RoomReservationResponse getRoomReservationsByUsername(
            String username, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<RoomReservation> pageRoomReservations = roomReservationRepository.findByUserId(user.getId(), pageDetails);

        RoomReservationResponse response = new RoomReservationResponse();
        response.setContent(pageRoomReservations.getContent());
        response.setPageNumber(pageRoomReservations.getNumber());
        response.setPageSize(pageRoomReservations.getSize());
        response.setTotalElements(pageRoomReservations.getTotalElements());
        response.setTotalPages(pageRoomReservations.getTotalPages());
        response.setLastPage(pageRoomReservations.isLast());
        return response;
    }

    public List<RoomReservation> getPendingReservations() {
        return roomReservationRepository.findByStatus(ReservationStatus.PENDING);
    }

    public RoomReservation updateReservationStatus(Long id, ReservationStatus status) {
        RoomReservation reservation = getRoomReservationById(id);
        reservation.setStatus(status);
        return roomReservationRepository.save(reservation);
    }
}
