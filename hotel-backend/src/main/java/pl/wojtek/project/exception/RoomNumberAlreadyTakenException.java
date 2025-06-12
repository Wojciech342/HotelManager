package pl.wojtek.project.exception;

public class RoomNumberAlreadyTakenException extends RuntimeException {

    public RoomNumberAlreadyTakenException(Integer roomNumber) {
      super("Room with number " + roomNumber + "already exists");
    }
}
