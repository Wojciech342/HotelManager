package pl.wojtek.project.model;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RoomSpecifications {

    public static Specification<Room> hasTypes(List<String> types) {
        return (root, query, cb) -> root.get("type").in(types);
    }

    public static Specification<Room> hasMinRating(Double minRating) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("averageRating"), minRating);
    }

    public static Specification<Room> hasPriceLessThan(Double maxPrice) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("pricePerNight"), maxPrice);
    }

    public static Specification<Room> hasPriceGreaterThan(Double minPrice) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("pricePerNight"), minPrice);
    }
}

