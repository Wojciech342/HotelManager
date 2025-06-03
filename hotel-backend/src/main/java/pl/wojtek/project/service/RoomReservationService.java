package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wojtek.project.exception.ResourceNotFoundException;
import pl.wojtek.project.model.Room;
import pl.wojtek.project.model.RoomReservation;
import pl.wojtek.project.model.User;
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

    @Transactional
    public RoomReservation createRoomReservation(String username, Long roomId, RoomReservation roomReservation) {
        // Fetch the user and room from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));

        // Validate reservation dates
        if (roomReservation.getStartDate().isAfter(roomReservation.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        //Check room availability
//        boolean isAvailable = room.getRoomReservations().stream()
//                .noneMatch(reservation ->
//                        reservation.getStartDate().isBefore(roomReservation.getEndDate()) &&
//                                reservation.getEndDate().isAfter(roomReservation.getStartDate()));
//        if (!isAvailable) {
//            throw new IllegalArgumentException("Room is not available for the selected dates");
//        }

        // Set the user and room for the reservation
        roomReservation.setUser(user);
        roomReservation.setRoom(room);

        // Add the reservation to the user's and room's reservation lists
        user.getReservations().add(roomReservation);
        room.getRoomReservations().add(roomReservation);

        // Save the reservation
        return roomReservationRepository.save(roomReservation);
    }

    public List<RoomReservation> getAllRoomReservations() {
        return roomReservationRepository.findAll();
    }

    public RoomReservation getRoomReservationById(Long id) {
        RoomReservation roomReservation = roomReservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReservation", "id", id));
        return roomReservation;
    }

    @Transactional
    public RoomReservation updateRoomReservation(Long id, RoomReservation roomReservation) {
        // Fetch the existing reservation
        RoomReservation roomReservationFromDB = roomReservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReservation", "id", id));

        // Update fields
        if (roomReservation.getStartDate() != null) {
            roomReservationFromDB.setStartDate(roomReservation.getStartDate());
        }
        if (roomReservation.getEndDate() != null) {
            roomReservationFromDB.setEndDate(roomReservation.getEndDate());
        }
        if (roomReservation.getPrice() != 0) {
            roomReservationFromDB.setPrice(roomReservation.getPrice());
        }

        RoomReservation updatedRoomReservation = roomReservationRepository.save(roomReservationFromDB);
        return updatedRoomReservation;
    }

    @Transactional
    public void deleteRoomReservationById(Long id) {
        roomReservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReservation", "id", id));

        // orphanRemoval = true:
        // This ensures that removing a RoomReservation from the roomReservations list
        // in the Room entity automatically deletes it from the database.


        roomReservationRepository.deleteById(id);
    }

    public List<RoomReservation> getRoomReservationsByRoomId(Long roomId) {
        List<RoomReservation> roomReservations = roomReservationRepository
                .findByRoomId(roomId).orElseThrow(() -> new ResourceNotFoundException("RoomReservation", "roomId", roomId));

        return roomReservations;
    }
}
