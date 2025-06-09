package pl.wojtek.project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RoomReservation extends Reservation {
    @ManyToOne
    @JsonIgnoreProperties(value = {"roomReservations", "reviews"})
    private Room room;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private RoomReview review;

    private String imageUrl;

}
