package com.ticketflow.ticket_service.booking.application.mapper;

import com.ticketflow.ticket_service.booking.application.dto.request.CreateTicketRequest;
import com.ticketflow.ticket_service.booking.application.dto.request.UpdateTicketRequest;
import com.ticketflow.ticket_service.booking.application.dto.response.TicketResponse;
import com.ticketflow.ticket_service.booking.domain.model.Ticket;
import com.ticketflow.ticket_service.booking.domain.model.TicketStatus;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-11T22:22:17-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ITicketApplicationMapperImpl implements ITicketApplicationMapper {

    @Override
    public Ticket toDomain(CreateTicketRequest request) {
        if ( request == null ) {
            return null;
        }

        Ticket.TicketBuilder ticket = Ticket.builder();

        ticket.id( request.id() );
        ticket.eventId( request.eventId() );
        ticket.userId( request.userId() );

        ticket.deleted( false );

        return ticket.build();
    }

    @Override
    public TicketResponse toResponse(Ticket ticket) {
        if ( ticket == null ) {
            return null;
        }

        String id = null;
        String eventId = null;
        String userId = null;
        LocalDateTime purchaseDate = null;
        TicketStatus status = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = ticket.getId();
        eventId = ticket.getEventId();
        userId = ticket.getUserId();
        purchaseDate = ticket.getPurchaseDate();
        status = ticket.getStatus();
        createdAt = ticket.getCreatedAt();
        updatedAt = ticket.getUpdatedAt();

        TicketResponse ticketResponse = new TicketResponse( id, eventId, userId, purchaseDate, status, createdAt, updatedAt );

        return ticketResponse;
    }

    @Override
    public void updateDomainFromRequest(UpdateTicketRequest request, Ticket ticket) {
        if ( request == null ) {
            return;
        }

        ticket.setUserId( request.userId() );
    }
}
