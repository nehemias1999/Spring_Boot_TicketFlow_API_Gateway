package com.ticketflow.ticket_service.booking.domain.exception;

/**
 * Exception thrown when a ticket with the specified ID is not found
 * or has been soft-deleted.
 *
 * @author TicketFlow Team
 */
public class TicketNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code TicketNotFoundException} with a descriptive message.
     *
     * @param id the ticket ID that was not found
     */
    public TicketNotFoundException(String id) {
        super(String.format("Ticket with id '%s' not found", id));
    }
}
