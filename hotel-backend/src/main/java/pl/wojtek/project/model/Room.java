package pl.wojtek.project.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    private Integer number;
    private Integer capacity;
    private String status;
    private Double averageRating;
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
