package pl.wojtek.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.wojtek.project.model.RoomReview;
import pl.wojtek.project.service.RoomReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/room-reviews")
public class RoomReviewController {

    private final RoomReviewService roomReviewService;

    @Autowired
    public RoomReviewController(RoomReviewService roomReviewService) {
        this.roomReviewService = roomReviewService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoomReview> createRoomReview(
            @RequestParam Long roomReservationId,
            @RequestBody RoomReview review) {
        RoomReview roomReview = roomReviewService.createReview(roomReservationId, review);
        return new ResponseEntity<>(roomReview, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomReview>> getRoomReviews() {
        List<RoomReview> reviews = roomReviewService.getAllReviews();
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<RoomReview>> getRoomReviewsByRoomId(@PathVariable Long roomId) {
        List<RoomReview> reviews = roomReviewService.getReviewsByRoom(roomId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<List<RoomReview>> getReviewsByUsername(@PathVariable String username) {
        List<RoomReview> reviews = roomReviewService.getReviewsByUsername(username);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomReview> getReviewById(@PathVariable Long id) {
        RoomReview review = roomReviewService.getReviewById(id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoomReview> updateReview(@PathVariable Long id, @RequestBody RoomReview review) {
        RoomReview updatedReview = roomReviewService.updateReview(id, review);
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        roomReviewService.deleteReview(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}