package com.ticketflow.ticket_service.booking.domain.exception;

/**
 * Exception thrown when attempting to create a ticket with an ID that already exists.
 *
 * @author TicketFlow Team
 */
public class TicketAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new {@code TicketAlreadyExistsException} with a descriptive message.
     *
     * @param id the ticket ID that already exists
     */
    public TicketAlreadyExistsException(String id) {
        super(String.format("Ticket with id '%s' already exists", id));
    }
}
