/**
 * Spring configuration classes for dependency injection and framework setup.
 * 
 * <p>This package contains Spring @Configuration classes that wire together
 * the application components, configure external libraries, and set up
 * cross-cutting concerns like logging, transactions, and scheduling.
 * 
 * <p>Configuration classes:
 * <ul>
 *   <li>{@link io.thatworked.support.report.infrastructure.config.DomainConfiguration} - 
 *       Configures domain services and their dependencies, wiring port
 *       implementations to domain components</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.config.LoggerConfiguration} - 
 *       Sets up structured logging with appropriate log levels and formats
 *       for different environments</li>
 * </ul>
 * 
 * <p>Dependency injection setup:
 * <ul>
 *   <li>Bean definitions for all domain and application services</li>
 *   <li>Port-to-adapter wiring using constructor injection</li>
 *   <li>Conditional beans based on profiles (dev, test, prod)</li>
 *   <li>Property-based configuration with @ConfigurationProperties</li>
 * </ul>
 * 
 * <p>Framework configurations:
 * <ul>
 *   <li>JPA and Hibernate settings for optimal performance</li>
 *   <li>Transaction management with proper isolation levels</li>
 *   <li>Feign client configuration with timeouts and retries</li>
 *   <li>Async executor setup for report generation</li>
 *   <li>Scheduled task configuration for cleanup jobs</li>
 * </ul>
 * 
 * <p>Cross-cutting concerns:
 * <ul>
 *   <li>Exception handling with @ControllerAdvice</li>
 *   <li>Request/response logging interceptors</li>
 *   <li>Performance monitoring with metrics</li>
 *   <li>Health check endpoints configuration</li>
 *   <li>CORS settings for API access</li>
 * </ul>
 * 
 * <p>Environment-specific settings:
 * <ul>
 *   <li>Development - verbose logging, in-memory storage options</li>
 *   <li>Testing - mock external services, fast timeouts</li>
 *   <li>Production - optimized pools, external service integration</li>
 * </ul>
 */
package io.thatworked.support.report.infrastructure.config;