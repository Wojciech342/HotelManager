package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wojtek.project.exception.ResourceNotFoundException;
import pl.wojtek.project.model.Room;
import pl.wojtek.project.model.RoomReservation;
import pl.wojtek.project.model.RoomReview;
import pl.wojtek.project.model.User;
import pl.wojtek.project.repository.RoomRepository;
import pl.wojtek.project.repository.RoomReservationRepository;
import pl.wojtek.project.repository.RoomReviewRepository;
import pl.wojtek.project.repository.UserRepository;

import java.util.List;

@Service
public class RoomReviewService {

    private final RoomReviewRepository roomReviewRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomReservationRepository roomReservationRepository;

    @Autowired
    public RoomReviewService(RoomReviewRepository roomReviewRepository,
                             RoomRepository roomRepository,
                             UserRepository userRepository,
                             RoomReservationRepository roomReservationRepository) {
        this.roomReviewRepository = roomReviewRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomReservationRepository = roomReservationRepository;
    }

    @Transactional
    public RoomReview createReview(Long roomReservationId, RoomReview review) {
        RoomReservation roomReservation = roomReservationRepository.findById(roomReservationId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReservation", "id", roomReservationId));
        Room room = roomRepository.findById(roomReservation.getRoom().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomReservation.getRoom().getId()));
        User user = userRepository.findByUsername(review.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", review.getUsername()));

        review.setRoom(room);
        RoomReview savedReview = roomReviewRepository.save(review);

        user.getRoomReviews().add(savedReview);
        userRepository.save(user);

        roomReservation.setReview(savedReview);
        roomReservationRepository.save(roomReservation);

        updateAverageRoomRating(room);
        return savedReview;
    }

    private void updateAverageRoomRating(Room room) {
        List<RoomReview> reviews = roomReviewRepository.findByRoomId(room.getId());

        double avg = reviews.stream()
                .mapToDouble(RoomReview::getRating)
                .average()
                .orElse(0.0);

        room.setAverageRating(avg);
        roomRepository.save(room);
    }

    public List<RoomReview> getAllReviews() {
        return roomReviewRepository.findAll();
    }

    public List<RoomReview> getReviewsByRoom(Long roomId) {
        roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));

        return roomReviewRepository.findByRoomId(roomId);
    }

    public List<RoomReview> getReviewsByUsername(String username) {
        userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return roomReviewRepository.findByUsername(username);
    }

    public RoomReview getReviewById(Long roomReviewId) {
        return roomReviewRepository.findById(roomReviewId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReview", "id", roomReviewId));
    }

    public RoomReview updateReview(Long id, RoomReview updatedReview) {
        RoomReview review = roomReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReview", "id", id));
        if(updatedReview.getDescription() != null) {
            review.setDescription(updatedReview.getDescription());
        }
        if(updatedReview.getRating() != null) {
            review.setRating(updatedReview.getRating());
        }
        return roomReviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        roomReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReview", "id", id));

        roomReviewRepository.deleteById(id);
    }
}