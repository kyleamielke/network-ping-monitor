/**
 * Infrastructure adapters implementing domain ports.
 * 
 * <p>Contains adapter implementations that bridge between the domain layer
 * and technical infrastructure components.</p>
 * 
 * <h2>Key Adapters:</h2>
 * <ul>
 *   <li>{@link io.thatworked.support.search.infrastructure.adapter.DomainLoggerAdapter} - Implements DomainLogger port</li>
 *   <li>{@link io.thatworked.support.search.infrastructure.adapter.CacheAdapter} - Implements CachePort using Spring Cache</li>
 *   <li>{@link io.thatworked.support.search.infrastructure.adapter.ServiceSearchProviderAdapter} - Implements SearchProvider port</li>
 * </ul>
 */
package io.thatworked.support.search.infrastructure.adapter;