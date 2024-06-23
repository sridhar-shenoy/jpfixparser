package com.jpm.fixparser;

import com.jpm.exception.MalformedFixMessageException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ParsingMalformedMessageTest extends FixMessageTestBase {

    @Test
    public void throwExceptionForMissingTagAtTheStartOfMessage() {
        try {
            parser.parse(getBytes("=FIX.4.4\u000110=092\u0001"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Value of Tag is Incorrect", e.getMessage());
            return;
        }
        fail("Should have thrown an exception");
    }

    @Test
    public void throwExceptionForMissingTagAtTheEndOfMessage() {
        try {
            parser.parse(getBytes("8=FIX.4.4\u0001=092\u0001"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Value of Tag is Incorrect", e.getMessage());
            return;
        }
        fail("Should have thrown an exception");
    }

    @Test
    public void throwExceptionForMissingTagAnywhereInTheMessage() {
        try {
            parser.parse(getBytes("8=FIX.4.4\u0001=D\u000110=092\u0001"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Value of Tag is Incorrect", e.getMessage());
            return;
        }
        fail("Should have thrown an exception");
    }

    @Test
    public void throwExceptionForMissingEqualsChar() {
        try {
            parser.parse(getBytes("8FIX.4.4\u000110=092\u0001"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Unable to parse fix message due to malformed tag value pair", e.getMessage());
            return;
        }
        fail("Should have thrown an exception");
    }

    @Test
    public void throwExceptionForMissingValue() {
        try {
            parser.parse(getBytes("8=FIX.4.4\u000110=\u0001"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Unable to parse fix message due to missing value", e.getMessage());
            return;
        }
        fail("Should have thrown an exception");
    }

    @Test
    public void throwExceptionForDelimiterInTextFeild() {
        try {
            parser.parse(getBytes("8=FIX.4.4\u000158=dfhskjdhfskjhfskjdhf\u0001fdjdgfkjdfh\u0001"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Unable to parse fix message due to malformed tag value pair", e.getMessage());
            return;
        }
        fail("Should have thrown an exception");
    }

}
