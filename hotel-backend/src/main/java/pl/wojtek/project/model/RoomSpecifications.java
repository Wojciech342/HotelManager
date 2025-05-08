package pl.wojtek.project.model;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RoomSpecifications {

    public static Specification<Room> hasTypes(List<String> types) {
        return (root, query, cb) -> root.get("type").in(types);
    }

    public static Specification<Room> hasMinRating(Double minRating) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

    public static Specification<Room> hasPriceLessThan(Double maxPrice) {
        return (root, query, cb) ->
                cb.lessThan(root.get("pricePerNight"), maxPrice);
    }

    public static Specification<Room> hasPriceGreaterThan(Double minPrice) {
        return (root, query, cb) -> cb.greaterThan(root.get("pricePerNight"), minPrice);
    }
}

