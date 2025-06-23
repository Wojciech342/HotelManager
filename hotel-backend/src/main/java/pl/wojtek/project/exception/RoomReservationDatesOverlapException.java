package pl.wojtek.project.exception;

import lombok.Getter;
import pl.wojtek.project.model.Room;

import java.time.LocalDate;

@Getter
public class RoomReservationDatesOverlapException extends RuntimeException {

    private final Long roomId;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public RoomReservationDatesOverlapException(Room room, LocalDate startDate, LocalDate endDate) {
        super(String.format(
                "Room %d (%s) is already booked between %s and %s",
                room.getId(),
                room.getType(),
                startDate,
                endDate
        ));
        this.roomId = room.getId();
        this.startDate = startDate;
        this.endDate = endDate;
    }
}