package com.ticketflow.ticket_service.booking.infrastructure.adapter.out.persistence.mapper;

import com.ticketflow.ticket_service.booking.domain.model.Ticket;
import com.ticketflow.ticket_service.booking.infrastructure.adapter.out.persistence.TicketEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-11T22:22:17-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ITicketPersistenceMapperImpl implements ITicketPersistenceMapper {

    @Override
    public TicketEntity toEntity(Ticket ticket) {
        if ( ticket == null ) {
            return null;
        }

        TicketEntity.TicketEntityBuilder ticketEntity = TicketEntity.builder();

        ticketEntity.id( ticket.getId() );
        ticketEntity.eventId( ticket.getEventId() );
        ticketEntity.userId( ticket.getUserId() );
        ticketEntity.purchaseDate( ticket.getPurchaseDate() );
        ticketEntity.status( ticket.getStatus() );
        ticketEntity.deleted( ticket.isDeleted() );
        ticketEntity.createdAt( ticket.getCreatedAt() );
        ticketEntity.updatedAt( ticket.getUpdatedAt() );

        return ticketEntity.build();
    }

    @Override
    public Ticket toDomain(TicketEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Ticket.TicketBuilder ticket = Ticket.builder();

        ticket.id( entity.getId() );
        ticket.eventId( entity.getEventId() );
        ticket.userId( entity.getUserId() );
        ticket.purchaseDate( entity.getPurchaseDate() );
        ticket.status( entity.getStatus() );
        ticket.deleted( entity.isDeleted() );
        ticket.createdAt( entity.getCreatedAt() );
        ticket.updatedAt( entity.getUpdatedAt() );

        return ticket.build();
    }
}
