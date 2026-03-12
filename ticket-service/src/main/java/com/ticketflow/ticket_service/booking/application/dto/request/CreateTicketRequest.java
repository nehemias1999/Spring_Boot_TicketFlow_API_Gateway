package com.ticketflow.ticket_service.booking.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for purchasing a new ticket.
 *
 * @param id      the unique business identifier for the ticket (e.g., "TKT-001")
 * @param eventId the ID of the event to purchase a ticket for
 * @param userId  the ID of the user purchasing the ticket
 * @author TicketFlow Team
 */
public record CreateTicketRequest(

        @NotBlank
        @Size(max = 20)
        String id,

        @NotBlank
        @Size(max = 20)
        String eventId,

        @NotBlank
        @Size(max = 50)
        String userId
) {
}
