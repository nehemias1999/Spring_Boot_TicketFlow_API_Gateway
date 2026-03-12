package com.ticketflow.ticket_service.booking.infrastructure.adapter.out.persistence.mapper;

import com.ticketflow.ticket_service.booking.domain.model.Ticket;
import com.ticketflow.ticket_service.booking.infrastructure.adapter.out.persistence.TicketEntity;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for converting between the JPA {@link TicketEntity}
 * and the domain {@link Ticket} model.
 * <p>
 * This mapper isolates the infrastructure persistence layer from the domain,
 * ensuring that JPA annotations and entity concerns do not leak into
 * the core business logic. The implementation is generated at compile time
 * by the MapStruct annotation processor.
 * </p>
 *
 * @author TicketFlow Team
 */
@Mapper(componentModel = "spring")
public interface ITicketPersistenceMapper {

    /**
     * Converts a {@link Ticket} domain object to a {@link TicketEntity} JPA entity.
     *
     * @param ticket the domain object to convert
     * @return the corresponding JPA entity
     */
    TicketEntity toEntity(Ticket ticket);

    /**
     * Converts a {@link TicketEntity} JPA entity to a {@link Ticket} domain object.
     *
     * @param entity the JPA entity to convert
     * @return the corresponding domain object
     */
    Ticket toDomain(TicketEntity entity);
}
