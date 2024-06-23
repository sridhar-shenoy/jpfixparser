package com.jpm.fixparser;

import com.jpm.FixMessageTestBase;
import com.jpm.exception.MalformedFixMessageException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IncorrectlyPlacedDelimiterTest extends FixMessageTestBase {

    @Test
    public void throwExceptionWithoutDelimiterAtTheEndOfMessage() {
        try {
            parser.parse(getBytes("8=FIX.4.4\u000110=092"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Unable to parse due to missing delimiter", e.getMessage());
        }
    }

    @Test
    public void throwExceptionForRepeatedDelimiter() {
        try {
            parser.parse(getBytes("8=FIX.4.4\u0001\u000110=092\u0001"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Unable to parse fix message due to malformed tag value pair", e.getMessage());
            return;
        }
        fail("Should have thrown an exception");
    }

    @Test
    public void throwExceptionWithRepeatedDelimiterAtTheEndOfMessage() {
        try {
            parser.parse(getBytes("8=FIX.4.4\u000110=092\u0001\u0001"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Unable to parse fix message due to malformed tag value pair", e.getMessage());
            return;
        }
        fail("Should have thrown an exception");
    }

    @Test
    public void throwExceptionWithDelimiterAtTheStartOfMessage() {
        try {
            parser.parse(getBytes("\u00018=FIX.4.4\u000110=092\u0001"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Unable to parse fix message due to malformed tag value pair", e.getMessage());
            return;
        }
        fail("Should have thrown an exception");
    }

    @Test
    public void throwExceptionWithRepeatedDelimiterAtTheStartOfMessage() {
        try {
            parser.parse(getBytes("\u0001\u00018=FIX.4.4\u000110=092\u0001"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Unable to parse fix message due to malformed tag value pair", e.getMessage());
            return;
        }
        fail("Should have thrown an exception");
    }
}
