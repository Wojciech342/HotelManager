package pl.wojtek.project.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pl.wojtek.project.model.ReservationStatus;
import pl.wojtek.project.model.RoomReservation;
import pl.wojtek.project.message.response.RoomReservationResponse;
import pl.wojtek.project.service.RoomReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/room-reservations")
public class RoomReservationController {

    private final RoomReservationService roomReservationService;

    @Autowired
    public RoomReservationController(RoomReservationService roomReservationService) {
        this.roomReservationService = roomReservationService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoomReservation> createRoomReservation(
            @RequestParam Long roomId,
            @RequestBody RoomReservation roomReservation) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        RoomReservation createdRoomReservation = roomReservationService.createRoomReservation(username, roomId, roomReservation);
        return new ResponseEntity<>(createdRoomReservation, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomReservationResponse> getRoomReservations(
            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "startDate", required = false)  String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "asc", required = false) String sortOrder) {
        RoomReservationResponse reservations = roomReservationService.getRoomReservations(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoomReservation> getRoomReservationById(@PathVariable Long id) {
        RoomReservation roomReservation = roomReservationService.getRoomReservationById(id);
        return new ResponseEntity<>(roomReservation, HttpStatus.OK);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<RoomReservation>> getReservationsByRoomId(@PathVariable Long roomId) {
        List<RoomReservation> reservations = roomReservationService.getRoomReservationsByRoomId(roomId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/users/{username}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoomReservationResponse> getRoomReservationsByUsername(
            @PathVariable String username,
            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "startDate", required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "asc", required = false) String sortOrder) {
        RoomReservationResponse reservations = roomReservationService.getRoomReservationsByUsername(
                username, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoomReservation>> getPendingRoomReservations() {
        List<RoomReservation> pendingReservations = roomReservationService.getPendingReservations();
        return new ResponseEntity<>(pendingReservations, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomReservation> updateRoomReservationStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status) {
        RoomReservation updatedReservation = roomReservationService.updateReservationStatus(id, status);
        return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
    }

}
