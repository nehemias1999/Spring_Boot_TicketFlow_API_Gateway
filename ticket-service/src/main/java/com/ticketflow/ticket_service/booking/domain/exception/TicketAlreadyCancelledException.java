package com.ticketflow.ticket_service.booking.domain.exception;

/**
 * Exception thrown when attempting to cancel a ticket that is already cancelled.
 *
 * @author TicketFlow Team
 */
public class TicketAlreadyCancelledException extends RuntimeException {

    /**
     * Constructs a new {@code TicketAlreadyCancelledException} with a descriptive message.
     *
     * @param id the ticket ID that is already cancelled
     */
    public TicketAlreadyCancelledException(String id) {
        super(String.format("Ticket with id '%s' is already cancelled", id));
    }
}
