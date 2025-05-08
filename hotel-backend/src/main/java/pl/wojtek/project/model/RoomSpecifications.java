package pl.wojtek.project.model;

import org.springframework.data.jpa.domain.Specification;

public class RoomSpecifications {

    public static Specification<Room> hasType(String type) {
        return (root, query, cb) ->
                cb.equal(root.get("type"), type);
    }

    public static Specification<Room> hasMinRating(Double minRating) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

    public static Specification<Room> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, cb) ->
                cb.between(root.get("price"), minPrice, maxPrice);
    }
}

