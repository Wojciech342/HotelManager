package pl.wojtek.project.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wojtek.project.model.Room;
import pl.wojtek.project.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:4200")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<Room>> getRooms(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        List<Room> rooms = roomService.getFilteredRooms(type, minRating, minPrice, maxPrice);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable long id) {
        Room room = roomService.getRoomById(id);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room createdRoom = roomService.createRoom(room);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Room> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<Room> deleteAllRooms() {
        roomService.deleteAllRooms();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
