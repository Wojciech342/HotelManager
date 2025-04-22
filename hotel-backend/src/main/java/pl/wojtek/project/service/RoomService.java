package pl.wojtek.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.wojtek.project.model.Room;
import pl.wojtek.project.repository.RoomRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return rooms;
    }

    public Room getRoomById(long id) {
        Room room = roomRepository.findById(id).orElse(null);
        return room;
    }

    public Room createRoom(Room room) {
        Room createdRoom = roomRepository.save(room);
        return createdRoom;
    }
}
