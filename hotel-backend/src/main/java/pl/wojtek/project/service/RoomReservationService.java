package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.wojtek.project.exception.ResourceNotFoundException;
import pl.wojtek.project.exception.RoomReservationDatesOverlapException;
import pl.wojtek.project.model.ReservationStatus;
import pl.wojtek.project.model.Room;
import pl.wojtek.project.model.RoomReservation;
import pl.wojtek.project.model.User;
import pl.wojtek.project.payload.RoomReservationResponse;
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

        checkForOverlappingReservation(room, roomReservation);

        roomReservation.setUser(user);
        roomReservation.setRoom(room);
        roomReservation.setImageUrl(room.getImageUrl());
        roomReservation.setStatus(ReservationStatus.PENDING);

        return roomReservationRepository.save(roomReservation);
    }

    private void checkForOverlappingReservation(Room room, RoomReservation newReservation) {
        List<RoomReservation> activeReservations = roomReservationRepository.findByRoomId(room.getId())
                .orElse(List.of())
                .stream()
                .filter(res -> res.getStatus() != ReservationStatus.CANCELLED &&
                        res.getStatus() != ReservationStatus.REJECTED)
                .toList();

        for (RoomReservation existingRes : activeReservations) {
            if (newReservation.getStartDate().isBefore(existingRes.getEndDate()) &&
                    newReservation.getEndDate().isAfter(existingRes.getStartDate())) {
                throw new RoomReservationDatesOverlapException(
                        room,
                        existingRes.getStartDate(),
                        existingRes.getEndDate()
                );
            }
        }
    }

    public RoomReservationResponse getRoomReservations(Integer pageNumber, Integer pageSize,
                                                       String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<RoomReservation> pageRoomReservations = roomReservationRepository.findAll(pageDetails);
        return getRoomReservationResponse(pageRoomReservations);
    }

    private RoomReservationResponse getRoomReservationResponse(Page<RoomReservation> pageRoomReservations) {
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
        return roomReservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReservation", "id", id));
    }

    public List<RoomReservation> getRoomReservationsByRoomId(Long roomId) {
        return roomReservationRepository.findByRoomId(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReservation", "roomId", roomId));
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

        return getRoomReservationResponse(pageRoomReservations);
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
