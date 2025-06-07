package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.wojtek.project.exception.ResourceNotFoundException;
import pl.wojtek.project.model.Room;
import pl.wojtek.project.model.RoomReview;
import pl.wojtek.project.model.User;
import pl.wojtek.project.repository.RoomRepository;
import pl.wojtek.project.repository.RoomReviewRepository;
import pl.wojtek.project.repository.UserRepository;

import java.util.List;

@Service
public class RoomReviewService {

    private final RoomReviewRepository roomReviewRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoomReviewService(RoomReviewRepository roomReviewRepository, RoomRepository roomRepository, UserRepository userRepository) {
        this.roomReviewRepository = roomReviewRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public RoomReview createReview(String username, Long roomId, RoomReview review) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        review.setRoom(room);
        review.setUsername(username);
        RoomReview savedReview = roomReviewRepository.save(review);

        user.getRoomReviews().add(savedReview);
        userRepository.save(user);

        return savedReview;
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

    public RoomReview getReview(Long roomReviewId) {
        RoomReview roomReview = roomReviewRepository.findById(roomReviewId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReview", "id", roomReviewId));

        return roomReview;
    }

    public RoomReview updateReview(Long id, RoomReview updatedReview) {
        RoomReview review = roomReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReview", "id", id));
        review.setDescription(updatedReview.getDescription());
        review.setRating(updatedReview.getRating());
        return roomReviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        roomReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReview", "id", id));

        roomReviewRepository.deleteById(id);
    }
}