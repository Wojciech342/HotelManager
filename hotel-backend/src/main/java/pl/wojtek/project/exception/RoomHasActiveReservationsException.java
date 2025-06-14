package pl.wojtek.project.exception;

public class RoomHasActiveReservationsException extends RuntimeException {
    public RoomHasActiveReservationsException(String message) {
        super(message);
    }
}
