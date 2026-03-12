package com.ticketflow.ticket_service.shared.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA Auditing so that {@code @CreatedDate} and {@code @LastModifiedDate}
 * fields on entities are populated automatically by Spring Data.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
