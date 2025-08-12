package io.thatworked.support.gateway.config;

import graphql.language.StringValue;
import graphql.schema.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeParseException;

/**
 * Custom GraphQL scalar for java.time.Instant that serializes to ISO-8601 string.
 */
@Component
public class InstantScalar {

    public static final GraphQLScalarType INSTANT = GraphQLScalarType.newScalar()
        .name("DateTime")
        .description("ISO-8601 instant in UTC")
        .coercing(new Coercing<Instant, String>() {
            
            @Override
            public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                if (dataFetcherResult instanceof Instant) {
                    return ((Instant) dataFetcherResult).toString();
                }
                throw new CoercingSerializeException("Expected an Instant object");
            }

            @Override
            public Instant parseValue(Object input) throws CoercingParseValueException {
                try {
                    if (input instanceof String) {
                        return Instant.parse((String) input);
                    }
                    throw new CoercingParseValueException("Expected a String");
                } catch (DateTimeParseException e) {
                    throw new CoercingParseValueException("Invalid ISO-8601 instant: " + input, e);
                }
            }

            @Override
            public Instant parseLiteral(Object input) throws CoercingParseLiteralException {
                if (input instanceof StringValue) {
                    try {
                        return Instant.parse(((StringValue) input).getValue());
                    } catch (DateTimeParseException e) {
                        throw new CoercingParseLiteralException("Invalid ISO-8601 instant", e);
                    }
                }
                throw new CoercingParseLiteralException("Expected a StringValue");
            }
        })
        .build();
}