package com.ticketflow.ticket_service.booking.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for transferring a ticket to another user.
 *
 * @param userId the ID of the new ticket owner
 * @author TicketFlow Team
 */
public record UpdateTicketRequest(

        @NotBlank
        @Size(max = 50)
        String userId
) {
}
