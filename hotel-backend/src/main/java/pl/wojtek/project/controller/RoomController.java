package pl.wojtek.project.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.wojtek.project.model.Room;
import pl.wojtek.project.payload.RoomResponse;
import pl.wojtek.project.service.RoomService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<RoomResponse> getFilteredRooms(
            @RequestParam(value = "type", required = false) List<String> types,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "number", required = false)  String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "asc", required = false) String sortOrder) {
        RoomResponse roomResponse = roomService.getFilteredRooms(types, minRating, minPrice, maxPrice, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(roomResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable long id) {
        Room room = roomService.getRoomById(id);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Room> createRoom(
            @RequestPart("room") String roomJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Room room;
        System.out.println(roomJson);
        try {
            room = objectMapper.readValue(roomJson, Room.class);
            System.out.println(room.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        Room createdRoom = roomService.createRoomWithImage(room, image);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    @PutMapping("/{roomId}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Room> updateRoomImage(
            @PathVariable Long roomId,
            @RequestParam("image") MultipartFile image) throws IOException {
        Room updatedRoom = roomService.updateRoomImage(roomId, image);
        return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
    }

    @PutMapping("/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Room>> updateAllRoomsImage(
            @RequestParam("image") MultipartFile image) throws IOException {
        List<Room> updatedRooms = roomService.updateAllRoomsImage(image);
        return new ResponseEntity<>(updatedRooms, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Room> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Room> deleteAllRooms() {
        roomService.deleteAllRooms();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
