package com.ticketflow.ticket_service.booking.infrastructure.adapter.in.web;

import com.ticketflow.ticket_service.booking.application.dto.request.CreateTicketRequest;
import com.ticketflow.ticket_service.booking.application.dto.request.UpdateTicketRequest;
import com.ticketflow.ticket_service.booking.application.dto.response.TicketResponse;
import com.ticketflow.ticket_service.booking.domain.port.in.ITicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing CRUD and cancellation endpoints for ticket management.
 * <p>
 * This is an inbound adapter in the hexagonal architecture. It receives
 * HTTP requests, delegates to the {@link ITicketService}, and returns
 * appropriate HTTP responses.
 * </p>
 * <p>
 * Base path: {@code /api/v1/tickets}
 * </p>
 *
 * @author TicketFlow Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final ITicketService ticketServicePort;

    /**
     * Purchases a new ticket.
     *
     * @param request the validated creation request body
     * @return the created ticket with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request) {
        log.info("POST /api/v1/tickets - Request received to create ticket with id: {}", request.id());
        TicketResponse response = ticketServicePort.create(request);
        log.info("POST /api/v1/tickets - Ticket created successfully with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a single ticket by its unique ID.
     *
     * @param id the unique business identifier (path variable)
     * @return the ticket data with HTTP 200 status
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@PathVariable String id) {
        log.info("GET /api/v1/tickets/{} - Request received to retrieve ticket", id);
        TicketResponse response = ticketServicePort.getById(id);
        log.info("GET /api/v1/tickets/{} - Ticket retrieved successfully", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a paginated and filtered list of tickets.
     *
     * @param page    the page number (zero-based), defaults to 0
     * @param size    the number of items per page, defaults to 10
     * @param eventId optional filter by event ID
     * @param userId  optional filter by user ID
     * @param status  optional filter by ticket status
     * @param sortBy  the field to sort by, defaults to createdAt
     * @param sortDir the sort direction (asc/desc), defaults to desc
     * @return a page of ticket entries with HTTP 200 status
     */
    @GetMapping
    public ResponseEntity<Page<TicketResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String eventId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("GET /api/v1/tickets - Request received - page: {}, size: {}, eventId: {}, userId: {}, status: {}, sortBy: {}, sortDir: {}",
                page, size, eventId, userId, status, sortBy, sortDir);
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TicketResponse> response = ticketServicePort.getAll(eventId, userId, status, pageable);
        log.info("GET /api/v1/tickets - Retrieved {} tickets (page {} of {})",
                response.getNumberOfElements(), response.getNumber(), response.getTotalPages());
        return ResponseEntity.ok(response);
    }

    /**
     * Transfers a ticket to another user (updates userId).
     *
     * @param id      the unique business identifier (path variable)
     * @param request the validated update request body
     * @return the updated ticket with HTTP 200 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateTicketRequest request) {
        log.info("PUT /api/v1/tickets/{} - Request received to update ticket", id);
        TicketResponse response = ticketServicePort.update(id, request);
        log.info("PUT /api/v1/tickets/{} - Ticket updated successfully", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancels an active ticket.
     *
     * @param id the unique business identifier (path variable)
     * @return the cancelled ticket with HTTP 200 status
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TicketResponse> cancel(@PathVariable String id) {
        log.info("PATCH /api/v1/tickets/{}/cancel - Request received to cancel ticket", id);
        TicketResponse response = ticketServicePort.cancel(id);
        log.info("PATCH /api/v1/tickets/{}/cancel - Ticket cancelled successfully", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Soft-deletes a ticket by its unique ID.
     * <p>
     * The record is not physically removed from the database; it is
     * marked as deleted and excluded from future active queries.
     * </p>
     *
     * @param id the unique business identifier (path variable)
     * @return HTTP 204 No Content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("DELETE /api/v1/tickets/{} - Request received to soft-delete ticket", id);
        ticketServicePort.delete(id);
        log.info("DELETE /api/v1/tickets/{} - Ticket soft-deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
