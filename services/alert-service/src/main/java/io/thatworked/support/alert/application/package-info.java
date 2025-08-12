/**
 * Application layer orchestrating use cases and coordinating domain operations.
 * This layer contains application-specific business logic and use case implementations.
 * 
 * <p>Key principles:
 * <ul>
 *   <li>Use cases accept primitive types and simple DTOs as input</li>
 *   <li>Use cases return domain objects or simple values</li>
 *   <li>Transaction boundaries are defined at this layer</li>
 *   <li>Orchestrates calls to domain services and infrastructure</li>
 * </ul>
 */
package io.thatworked.support.alert.application;