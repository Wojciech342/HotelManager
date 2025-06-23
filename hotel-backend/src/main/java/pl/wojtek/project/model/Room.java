package pl.wojtek.project.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Positive(message = "Room number must be positive")
    private Integer number;

    @Positive(message = "Capacity must be positive")
    private Integer capacity;
    private Double averageRating = 0.0;

    @Positive(message = "Price must be positive")
    private Double pricePerNight;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    @OneToMany(mappedBy = "room", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<RoomReview> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnoreProperties("room")
    private List<RoomReservation> reservations = new ArrayList<>();

}
