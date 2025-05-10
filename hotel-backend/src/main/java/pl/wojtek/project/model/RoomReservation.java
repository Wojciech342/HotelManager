package pl.wojtek.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RoomReservation extends Reservation {
    @ManyToOne
    @JsonIgnore
    private Room room;

}
