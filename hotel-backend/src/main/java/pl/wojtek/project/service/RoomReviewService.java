package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        String currentUsername = getCurrentUsername();
        
        RoomReservation roomReservation = roomReservationRepository.findById(roomReservationId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReservation", "id", roomReservationId));
        
        if (!roomReservation.getUser().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You can only review your own reservations");
        }
        
        if (roomReservation.getReview() != null) {
            throw new IllegalStateException("This reservation already has a review");
        }
        
        Room room = roomReservation.getRoom();
        if (room == null) {
            throw new IllegalStateException("Cannot review a reservation for a deleted room");
        }
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

        // Set relationships
        review.setUser(user);
        review.setRoom(room);
        
        // Add to collections (bidirectional sync)
        user.getRoomReviews().add(review);
        room.getReviews().add(review);
        
        RoomReview savedReview = roomReviewRepository.save(review);

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

        return roomReviewRepository.findByUserUsername(username);
    }

    public RoomReview getReviewById(Long roomReviewId) {
        return roomReviewRepository.findById(roomReviewId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReview", "id", roomReviewId));
    }

    @Transactional
    public RoomReview updateReview(Long id, RoomReview updatedReview) {
        RoomReview review = roomReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReview", "id", id));
        
        authorizeReviewAccess(review);
        
        if (updatedReview.getDescription() != null) {
            review.setDescription(updatedReview.getDescription());
        }
        if (updatedReview.getRating() != null) {
            review.setRating(updatedReview.getRating());
            updateAverageRoomRating(review.getRoom());
        }
        return roomReviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        RoomReview roomReview = roomReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomReview", "id", id));

        authorizeReviewAccess(roomReview);

        // Clear reservation's review reference
        List<RoomReservation> reservationsWithReview = roomReservationRepository.findByReviewId(id);
        for (RoomReservation reservation : reservationsWithReview) {
            reservation.setReview(null);
        }
        roomReservationRepository.saveAll(reservationsWithReview);

        Room room = roomReview.getRoom();
        User user = roomReview.getUser();

        // Remove from collections - orphanRemoval will handle the delete
        room.getReviews().remove(roomReview);
        user.getRoomReviews().remove(roomReview);

        updateAverageRoomRating(room);
    }
    
    private void authorizeReviewAccess(RoomReview review) {
        String currentUsername = getCurrentUsername();
        boolean isOwner = currentUsername.equals(review.getUsername());
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You can only modify your own reviews");
        }
    }
    
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}