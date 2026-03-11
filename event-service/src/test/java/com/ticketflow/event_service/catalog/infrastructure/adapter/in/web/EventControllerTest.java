package com.ticketflow.event_service.catalog.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketflow.event_service.catalog.application.dto.request.CreateEventRequest;
import com.ticketflow.event_service.catalog.application.dto.request.UpdateEventRequest;
import com.ticketflow.event_service.catalog.application.dto.response.EventResponse;
import com.ticketflow.event_service.catalog.domain.exception.EventAlreadyExistsException;
import com.ticketflow.event_service.catalog.domain.exception.EventNotFoundException;
import com.ticketflow.event_service.catalog.domain.port.in.IEventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link EventController} using the Spring MVC test slice.
 * <p>
 * Only the web layer is loaded ({@code @WebMvcTest}). The {@link IEventService}
 * dependency is replaced by a Mockito mock ({@code @MockBean}), and the
 * {@link com.ticketflow.event_service.shared.infrastructure.exception.GlobalExceptionHandler}
 * is auto-detected as a {@code @RestControllerAdvice} within the same context.
 * </p>
 * <p>
 * Spring Cloud Config and Eureka are disabled via {@code @TestPropertySource}
 * to prevent external service connections during tests.
 * </p>
 */
@WebMvcTest(EventController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
@DisplayName("EventController — unit tests")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IEventService eventService;

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a sample {@link EventResponse} for the given ID.
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
     * Builds a valid {@link CreateEventRequest} for the given ID.
     *
     * @param id the event identifier
     * @return a valid CreateEventRequest
     */
    private static CreateEventRequest buildCreateRequest(String id) {
        return new CreateEventRequest(id, "Test Event", "Test description",
                "2026-10-15 20:00", "Test Location", BigDecimal.valueOf(100.00));
    }

    /**
     * Builds a valid {@link UpdateEventRequest} with updated field values.
     *
     * @return a valid UpdateEventRequest
     */
    private static UpdateEventRequest buildUpdateRequest() {
        return new UpdateEventRequest("Updated Title", "Updated description",
                "2026-11-20 18:00", "Updated Location", BigDecimal.valueOf(150.00));
    }

    // -------------------------------------------------------------------------
    // POST /api/v1/events
    // -------------------------------------------------------------------------

    /**
     * Tests for the {@code POST /api/v1/events} endpoint.
     */
    @Nested
    @DisplayName("POST /api/v1/events")
    class Create {

        /**
         * Verifies that a valid request returns HTTP 201 and the created event body.
         */
        @Test
        @DisplayName("should return 201 Created with EventResponse body on success")
        void create_success_returns201() throws Exception {
            CreateEventRequest request = buildCreateRequest("EVT-001");
            EventResponse response = buildResponse("EVT-001");

            when(eventService.create(any())).thenReturn(response);

            mockMvc.perform(post("/api/v1/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value("EVT-001"))
                    .andExpect(jsonPath("$.title").value("Test Event"))
                    .andExpect(jsonPath("$.location").value("Test Location"));
        }

        /**
         * Verifies that HTTP 409 Conflict is returned when the event ID already exists.
         */
        @Test
        @DisplayName("should return 409 Conflict when event ID already exists")
        void create_conflict_returns409() throws Exception {
            CreateEventRequest request = buildCreateRequest("EVT-001");

            when(eventService.create(any())).thenThrow(new EventAlreadyExistsException("EVT-001"));

            mockMvc.perform(post("/api/v1/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.message").exists());
        }

        /**
         * Verifies that HTTP 400 Bad Request is returned when the request body
         * fails bean validation constraints (blank ID, short title, negative price).
         */
        @Test
        @DisplayName("should return 400 Bad Request when request body fails validation")
        void create_validationError_returns400() throws Exception {
            // blank id, title too short, negative basePrice
            String invalidBody = """
                    {
                      "id": "",
                      "title": "a",
                      "description": "",
                      "date": "",
                      "location": "",
                      "basePrice": -1
                    }
                    """;

            mockMvc.perform(post("/api/v1/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/events/{id}
    // -------------------------------------------------------------------------

    /**
     * Tests for the {@code GET /api/v1/events/{id}} endpoint.
     */
    @Nested
    @DisplayName("GET /api/v1/events/{id}")
    class GetById {

        /**
         * Verifies that HTTP 200 OK with the event body is returned when the event exists.
         */
        @Test
        @DisplayName("should return 200 OK with EventResponse when event is found")
        void getById_success_returns200() throws Exception {
            EventResponse response = buildResponse("EVT-001");
            when(eventService.getById("EVT-001")).thenReturn(response);

            mockMvc.perform(get("/api/v1/events/EVT-001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("EVT-001"))
                    .andExpect(jsonPath("$.title").value("Test Event"));
        }

        /**
         * Verifies that HTTP 404 Not Found is returned when the event does not exist.
         */
        @Test
        @DisplayName("should return 404 Not Found when event does not exist")
        void getById_notFound_returns404() throws Exception {
            when(eventService.getById("EVT-999")).thenThrow(new EventNotFoundException("EVT-999"));

            mockMvc.perform(get("/api/v1/events/EVT-999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/events
    // -------------------------------------------------------------------------

    /**
     * Tests for the {@code GET /api/v1/events} endpoint.
     */
    @Nested
    @DisplayName("GET /api/v1/events")
    class GetAll {

        /**
         * Verifies that HTTP 200 OK with a page of events is returned
         * using the default page/size parameters.
         */
        @Test
        @DisplayName("should return 200 OK with paginated EventResponse list using default params")
        void getAll_defaultParams_returns200() throws Exception {
            EventResponse response = buildResponse("EVT-001");
            Page<EventResponse> page = new PageImpl<>(List.of(response));

            when(eventService.getAll(any(), any(), any())).thenReturn(page);

            mockMvc.perform(get("/api/v1/events"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value("EVT-001"))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        /**
         * Verifies that HTTP 200 OK with a page of events is returned
         * when custom page and size query parameters are provided.
         */
        @Test
        @DisplayName("should return 200 OK with correct pagination when custom page/size is provided")
        void getAll_customPagination_returns200() throws Exception {
            Page<EventResponse> emptyPage = new PageImpl<>(List.of());
            when(eventService.getAll(any(), any(), any())).thenReturn(emptyPage);

            mockMvc.perform(get("/api/v1/events")
                            .param("page", "2")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty());
        }

        /**
         * Verifies that title and location query params are accepted and forwarded.
         */
        @Test
        @DisplayName("should return 200 OK when title and location filter params are provided")
        void getAll_withFilters_returns200() throws Exception {
            EventResponse response = buildResponse("EVT-001");
            Page<EventResponse> page = new PageImpl<>(List.of(response));

            when(eventService.getAll(any(), any(), any())).thenReturn(page);

            mockMvc.perform(get("/api/v1/events")
                            .param("title", "Test")
                            .param("location", "Madrid"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value("EVT-001"));
        }
    }

    // -------------------------------------------------------------------------
    // PUT /api/v1/events/{id}
    // -------------------------------------------------------------------------

    /**
     * Tests for the {@code PUT /api/v1/events/{id}} endpoint.
     */
    @Nested
    @DisplayName("PUT /api/v1/events/{id}")
    class Update {

        /**
         * Verifies that HTTP 200 OK with the updated event body is returned on success.
         */
        @Test
        @DisplayName("should return 200 OK with updated EventResponse on success")
        void update_success_returns200() throws Exception {
            UpdateEventRequest request = buildUpdateRequest();
            EventResponse response = new EventResponse(
                    "EVT-001", "Updated Title", "Updated description",
                    "2026-11-20 18:00", "Updated Location",
                    BigDecimal.valueOf(150.00), LocalDateTime.now(), LocalDateTime.now());

            when(eventService.update(eq("EVT-001"), any())).thenReturn(response);

            mockMvc.perform(put("/api/v1/events/EVT-001")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("EVT-001"))
                    .andExpect(jsonPath("$.title").value("Updated Title"));
        }

        /**
         * Verifies that HTTP 404 Not Found is returned when the target event does not exist.
         */
        @Test
        @DisplayName("should return 404 Not Found when event to update does not exist")
        void update_notFound_returns404() throws Exception {
            UpdateEventRequest request = buildUpdateRequest();

            when(eventService.update(eq("EVT-999"), any()))
                    .thenThrow(new EventNotFoundException("EVT-999"));

            mockMvc.perform(put("/api/v1/events/EVT-999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        /**
         * Verifies that HTTP 400 Bad Request is returned when the update request
         * body fails bean validation constraints (blank title, negative price).
         */
        @Test
        @DisplayName("should return 400 Bad Request when update request body fails validation")
        void update_validationError_returns400() throws Exception {
            String invalidBody = """
                    {
                      "title": "",
                      "description": "",
                      "date": "",
                      "location": "",
                      "basePrice": -10
                    }
                    """;

            mockMvc.perform(put("/api/v1/events/EVT-001")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    // -------------------------------------------------------------------------
    // DELETE /api/v1/events/{id}
    // -------------------------------------------------------------------------

    /**
     * Tests for the {@code DELETE /api/v1/events/{id}} endpoint.
     */
    @Nested
    @DisplayName("DELETE /api/v1/events/{id}")
    class Delete {

        /**
         * Verifies that HTTP 204 No Content is returned after a successful soft-delete.
         */
        @Test
        @DisplayName("should return 204 No Content when event is soft-deleted successfully")
        void delete_success_returns204() throws Exception {
            doNothing().when(eventService).delete("EVT-001");

            mockMvc.perform(delete("/api/v1/events/EVT-001"))
                    .andExpect(status().isNoContent());

            verify(eventService).delete("EVT-001");
        }

        /**
         * Verifies that HTTP 404 Not Found is returned when the event to delete does not exist.
         */
        @Test
        @DisplayName("should return 404 Not Found when event to delete does not exist")
        void delete_notFound_returns404() throws Exception {
            doThrow(new EventNotFoundException("EVT-999")).when(eventService).delete("EVT-999");

            mockMvc.perform(delete("/api/v1/events/EVT-999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}
