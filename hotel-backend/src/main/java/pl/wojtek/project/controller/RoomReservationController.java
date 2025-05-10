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

    @PostMapping
    public ResponseEntity<RoomReservation> createRoomReservation(
            @RequestParam Long userId,
            @RequestParam Long roomId,
            @RequestBody RoomReservation roomReservation) {
        RoomReservation createdRoomReservation = roomReservationService.createRoomReservation(userId, roomId, roomReservation);
        return new ResponseEntity<>(createdRoomReservation, HttpStatus.CREATED);
    }

}
