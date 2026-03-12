package com.ticketflow.ticket_service.booking.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.domain.Specification;

/**
 * JPA Specifications for {@link TicketEntity} to support dynamic filtering.
 *
 * @author TicketFlow Team
 */
public class TicketSpecification {

    private TicketSpecification() {}

    /**
     * Filters out soft-deleted tickets.
     *
     * @return a specification that matches only non-deleted tickets
     */
    public static Specification<TicketEntity> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    /**
     * Filters tickets by exact event ID match.
     *
     * @param eventId the event ID to filter by
     * @return a specification matching the given eventId
     */
    public static Specification<TicketEntity> eventIdEquals(String eventId) {
        return (root, query, cb) -> cb.equal(root.get("eventId"), eventId);
    }

    /**
     * Filters tickets by exact user ID match.
     *
     * @param userId the user ID to filter by
     * @return a specification matching the given userId
     */
    public static Specification<TicketEntity> userIdEquals(String userId) {
        return (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }

    /**
     * Filters tickets by exact status match (case-insensitive).
     *
     * @param status the status string to filter by
     * @return a specification matching the given status
     */
    public static Specification<TicketEntity> statusEquals(String status) {
        return (root, query, cb) -> cb.equal(
                cb.upper(root.get("status").as(String.class)),
                status.toUpperCase()
        );
    }
}
