/**
 * Use case implementations.
 * 
 * Each use case represents a single user action:
 * - One class per use case
 * - Clear, focused responsibility
 * - Accepts primitives, returns domain objects
 * - Orchestrates domain services
 * 
 * No @Transactional annotations - let infrastructure handle it.
 */
package io.thatworked.support.device.application.usecase;