package com.ticketflow.event_service.catalog.application.service;

import com.ticketflow.event_service.catalog.application.dto.request.CreateEventRequest;
import com.ticketflow.event_service.catalog.application.dto.request.UpdateEventRequest;
import com.ticketflow.event_service.catalog.application.dto.response.EventResponse;
import com.ticketflow.event_service.catalog.application.mapper.IEventApplicationMapper;
import com.ticketflow.event_service.catalog.domain.exception.EventAlreadyExistsException;
import com.ticketflow.event_service.catalog.domain.exception.EventNotFoundException;
import com.ticketflow.event_service.catalog.domain.model.Event;
import com.ticketflow.event_service.catalog.domain.port.out.IEventPersistencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EventService}.
 * <p>
 * All dependencies ({@link IEventPersistencePort} and {@link IEventApplicationMapper})
 * are mocked with Mockito so that only the service business logic is tested in isolation.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventService — unit tests")
class EventServiceTest {

    @Mock
    private IEventPersistencePort eventPersistencePort;

    @Mock
    private IEventApplicationMapper eventApplicationMapper;

    @InjectMocks
    private EventService eventService;

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a sample {@link Event} domain object for the given ID.
     *
     * @param id the event identifier
     * @return a pre-populated Event
     */
    private static Event buildEvent(String id) {
        return Event.builder()
                .id(id)
                .title("Test Event")
                .description("Test description")
                .date("2026-10-15 20:00")
                .location("Test Location")
                .basePrice(BigDecimal.valueOf(100.00))
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Builds a sample {@link EventResponse} DTO for the given ID.
     *
     * @param id the event identifier
     * @return a pre-populated EventResponse
     */
    private static EventResponse buildResponse(String id) {
        return new EventResponse(id, "Test Event", "Test description",
                "2026-10-15 20:00", "Test Location",
                BigDecimal.valueOf(100.00), LocalDateTime.now(), null);
    }

    /**
     * Builds a sample {@link CreateEventRequest} for the given ID.
     *
     * @param id the event identifier
     * @return a valid CreateEventRequest
     */
    private static CreateEventRequest buildCreateRequest(String id) {
        return new CreateEventRequest(id, "Test Event", "Test description",
                "2026-10-15 20:00", "Test Location", BigDecimal.valueOf(100.00));
    }

    /**
     * Builds a sample {@link UpdateEventRequest} with updated field values.
     *
     * @return a valid UpdateEventRequest
     */
    private static UpdateEventRequest buildUpdateRequest() {
        return new UpdateEventRequest("Updated Title", "Updated description",
                "2026-11-20 18:00", "Updated Location", BigDecimal.valueOf(150.00));
    }

    // -------------------------------------------------------------------------
    // create()
    // -------------------------------------------------------------------------

    /**
     * Tests for the {@link EventService#create(CreateEventRequest)} method.
     */
    @Nested
    @DisplayName("create()")
    class Create {

        /**
         * Verifies that a new event is persisted and the response DTO is returned
         * when no active event with the same ID exists.
         */
        @Test
        @DisplayName("should create and return EventResponse when ID does not exist")
        void create_success() {
            // given
            CreateEventRequest request = buildCreateRequest("EVT-001");
            Event domain = buildEvent("EVT-001");
            EventResponse response = buildResponse("EVT-001");

            when(eventPersistencePort.existsByIdAndDeletedFalse("EVT-001")).thenReturn(false);
            when(eventApplicationMapper.toDomain(request)).thenReturn(domain);
            when(eventPersistencePort.save(domain)).thenReturn(domain);
            when(eventApplicationMapper.toResponse(domain)).thenReturn(response);

            // when
            EventResponse result = eventService.create(request);

            // then
            assertThat(result).isEqualTo(response);
            verify(eventPersistencePort).save(domain);
        }

        /**
         * Verifies that {@link EventAlreadyExistsException} is thrown and no save
         * is attempted when an active event with the same ID already exists.
         */
        @Test
        @DisplayName("should throw EventAlreadyExistsException when ID already exists")
        void create_alreadyExists_throwsException() {
            // given
            CreateEventRequest request = buildCreateRequest("EVT-001");
            when(eventPersistencePort.existsByIdAndDeletedFalse("EVT-001")).thenReturn(true);

            // when / then
            assertThatThrownBy(() -> eventService.create(request))
                    .isInstanceOf(EventAlreadyExistsException.class)
                    .hasMessageContaining("EVT-001");

            verify(eventPersistencePort, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // getById()
    // -------------------------------------------------------------------------

    /**
     * Tests for the {@link EventService#getById(String)} method.
     */
    @Nested
    @DisplayName("getById()")
    class GetById {

        /**
         * Verifies that the correct {@link EventResponse} is returned when an
         * active event with the given ID is found.
         */
        @Test
        @DisplayName("should return EventResponse when event is found by ID")
        void getById_success() {
            // given
            Event domain = buildEvent("EVT-001");
            EventResponse response = buildResponse("EVT-001");

            when(eventPersistencePort.findByIdAndDeletedFalse("EVT-001"))
                    .thenReturn(Optional.of(domain));
            when(eventApplicationMapper.toResponse(domain)).thenReturn(response);

            // when
            EventResponse result = eventService.getById("EVT-001");

            // then
            assertThat(result).isEqualTo(response);
        }

        /**
         * Verifies that {@link EventNotFoundException} is thrown when no active
         * event with the given ID exists.
         */
        @Test
        @DisplayName("should throw EventNotFoundException when event is not found")
        void getById_notFound_throwsException() {
            // given
            when(eventPersistencePort.findByIdAndDeletedFalse("EVT-999"))
                    .thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> eventService.getById("EVT-999"))
                    .isInstanceOf(EventNotFoundException.class)
                    .hasMessageContaining("EVT-999");
        }
    }

    // -------------------------------------------------------------------------
    // getAll()
    // -------------------------------------------------------------------------

    /**
     * Tests for the {@link EventService#getAll(org.springframework.data.domain.Pageable)} method.
     */
    @Nested
    @DisplayName("getAll()")
    class GetAll {

        /**
         * Verifies that a page of {@link EventResponse} objects is returned
         * when active events exist in the store.
         */
        @Test
        @DisplayName("should return paginated EventResponse list when active events exist")
        void getAll_success() {
            // given
            PageRequest pageable = PageRequest.of(0, 10);
            Event domain = buildEvent("EVT-001");
            EventResponse response = buildResponse("EVT-001");
            Page<Event> domainPage = new PageImpl<>(List.of(domain));

            when(eventPersistencePort.findAllByFilters(null, null, pageable)).thenReturn(domainPage);
            when(eventApplicationMapper.toResponse(domain)).thenReturn(response);

            // when
            Page<EventResponse> result = eventService.getAll(null, null, pageable);

            // then
            assertThat(result.getContent()).containsExactly(response);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        /**
         * Verifies that an empty page is returned when no active events exist.
         */
        @Test
        @DisplayName("should return empty page when no active events exist")
        void getAll_empty() {
            // given
            PageRequest pageable = PageRequest.of(0, 10);
            when(eventPersistencePort.findAllByFilters(null, null, pageable)).thenReturn(Page.empty());

            // when
            Page<EventResponse> result = eventService.getAll(null, null, pageable);

            // then
            assertThat(result.getContent()).isEmpty();
        }

        /**
         * Verifies that title and location filters are forwarded to the persistence port.
         */
        @Test
        @DisplayName("should forward title and location filters to persistence port")
        void getAll_withFilters() {
            // given
            PageRequest pageable = PageRequest.of(0, 10);
            Event domain = buildEvent("EVT-001");
            EventResponse response = buildResponse("EVT-001");
            Page<Event> domainPage = new PageImpl<>(List.of(domain));

            when(eventPersistencePort.findAllByFilters("Test", "Location", pageable)).thenReturn(domainPage);
            when(eventApplicationMapper.toResponse(domain)).thenReturn(response);

            // when
            Page<EventResponse> result = eventService.getAll("Test", "Location", pageable);

            // then
            assertThat(result.getContent()).containsExactly(response);
            verify(eventPersistencePort).findAllByFilters("Test", "Location", pageable);
        }
    }

    // -------------------------------------------------------------------------
    // update()
    // -------------------------------------------------------------------------

    /**
     * Tests for the {@link EventService#update(String, UpdateEventRequest)} method.
     */
    @Nested
    @DisplayName("update()")
    class Update {

        /**
         * Verifies that the mapper applies the update to the existing domain object
         * and the updated event is persisted and returned.
         */
        @Test
        @DisplayName("should update and return EventResponse when event is found")
        void update_success() {
            // given
            UpdateEventRequest request = buildUpdateRequest();
            Event existing = buildEvent("EVT-001");
            EventResponse response = buildResponse("EVT-001");

            when(eventPersistencePort.findByIdAndDeletedFalse("EVT-001"))
                    .thenReturn(Optional.of(existing));
            doNothing().when(eventApplicationMapper).updateDomainFromRequest(eq(request), eq(existing));
            when(eventPersistencePort.update(existing)).thenReturn(existing);
            when(eventApplicationMapper.toResponse(existing)).thenReturn(response);

            // when
            EventResponse result = eventService.update("EVT-001", request);

            // then
            assertThat(result).isEqualTo(response);
            verify(eventApplicationMapper).updateDomainFromRequest(request, existing);
            verify(eventPersistencePort).update(existing);
        }

        /**
         * Verifies that {@link EventNotFoundException} is thrown and no update
         * is attempted when the target event does not exist.
         */
        @Test
        @DisplayName("should throw EventNotFoundException when event to update is not found")
        void update_notFound_throwsException() {
            // given
            UpdateEventRequest request = buildUpdateRequest();
            when(eventPersistencePort.findByIdAndDeletedFalse("EVT-999"))
                    .thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> eventService.update("EVT-999", request))
                    .isInstanceOf(EventNotFoundException.class)
                    .hasMessageContaining("EVT-999");

            verify(eventPersistencePort, never()).update(any());
        }
    }

    // -------------------------------------------------------------------------
    // delete()
    // -------------------------------------------------------------------------

    /**
     * Tests for the {@link EventService#delete(String)} method.
     */
    @Nested
    @DisplayName("delete()")
    class Delete {

        /**
         * Verifies that the {@code deleted} flag is set to {@code true} and
         * the event is persisted after a soft-delete call.
         */
        @Test
        @DisplayName("should set deleted flag to true and persist when event is found")
        void delete_success() {
            // given
            Event existing = buildEvent("EVT-001");
            when(eventPersistencePort.findByIdAndDeletedFalse("EVT-001"))
                    .thenReturn(Optional.of(existing));

            // when
            eventService.delete("EVT-001");

            // then
            assertThat(existing.isDeleted()).isTrue();
            verify(eventPersistencePort).update(existing);
        }

        /**
         * Verifies that {@link EventNotFoundException} is thrown and no update
         * is attempted when the target event does not exist.
         */
        @Test
        @DisplayName("should throw EventNotFoundException when event to delete is not found")
        void delete_notFound_throwsException() {
            // given
            when(eventPersistencePort.findByIdAndDeletedFalse("EVT-999"))
                    .thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> eventService.delete("EVT-999"))
                    .isInstanceOf(EventNotFoundException.class)
                    .hasMessageContaining("EVT-999");

            verify(eventPersistencePort, never()).update(any());
        }
    }
}
