package pl.wojtek.project.model;


import jakarta.persistence.*;
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

    private int number;
    private int capacity;
    private String status;
    private double averageRating;
    private double pricePerNight;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    @OneToMany
    private List<RoomReview> reviews = new ArrayList<>();
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomReservation> roomReservations = new ArrayList<>();

}
