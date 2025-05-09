package pl.wojtek.project.model;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomReservation extends Reservation {
    @ManyToOne
    private Room room;

}
