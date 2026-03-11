package com.ticketflow.event_service.catalog.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.domain.Specification;

/**
 * JPA Specifications for {@link EventEntity} to support dynamic filtering.
 */
public class EventSpecification {

    private EventSpecification() {}

    public static Specification<EventEntity> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    public static Specification<EventEntity> titleContains(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<EventEntity> locationContains(String location) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }
}
