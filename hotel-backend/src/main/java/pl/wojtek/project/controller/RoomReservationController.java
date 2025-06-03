package pl.wojtek.project.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wojtek.project.model.RoomReservation;
import pl.wojtek.project.service.RoomReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/roomReservations")
@CrossOrigin(origins = "http://localhost:4200")
public class RoomReservationController {

    private final RoomReservationService roomReservationService;

    @Autowired
    public RoomReservationController(RoomReservationService roomReservationService) {
        this.roomReservationService = roomReservationService;
    }

    @GetMapping
    public ResponseEntity<List<RoomReservation>> getAllRoomReservations() {
        List<RoomReservation> reservations = roomReservationService.getAllRoomReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomReservation> getRoomReservationById(@PathVariable Long id) {
        RoomReservation roomReservation = roomReservationService.getRoomReservationById(id);
        return new ResponseEntity<>(roomReservation, HttpStatus.OK);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<RoomReservation>> getReservationsByRoomId(@PathVariable Long roomId) {
        List<RoomReservation> reservations = roomReservationService.getRoomReservationsByRoomId(roomId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RoomReservation> createRoomReservation(
            @RequestParam String username,
            @RequestParam Long roomId,
            @RequestBody RoomReservation roomReservation) {
        RoomReservation createdRoomReservation = roomReservationService.createRoomReservation(username, roomId, roomReservation);
        return new ResponseEntity<>(createdRoomReservation, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<RoomReservation> updateRoomReservation(
            @PathVariable Long id,
            @RequestBody RoomReservation roomReservation
    ) {
        RoomReservation updatedRoomReservation = roomReservationService.updateRoomReservation(id, roomReservation);
        return new ResponseEntity<>(updatedRoomReservation, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RoomReservation> deleteRoomReservation(@PathVariable Long id) {
        roomReservationService.deleteRoomReservationById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
