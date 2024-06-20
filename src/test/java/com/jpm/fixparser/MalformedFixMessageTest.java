package com.jpm.fixparser;

import com.jpm.exception.MalformedFixMessageException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MalformedFixMessageTest extends FixMessageTestBase {

    @Test
    public void throwExceptionForMissingTagAtTheStartOfMessage() {
        try {
            parser.parse(getBytes("=FIX.4.4\u000110=092\u0001"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Unable to parse fix message due to malformed tag value pair", e.getMessage());
        }
    }

    @Test
    public void throwExceptionForMissingTagAtTheEndOfMessage() {
        try {
            parser.parse(getBytes("8=FIX.4.4\u0001=092\u0001"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Unable to parse fix message due to malformed tag value pair", e.getMessage());
        }
    }

    @Test
    public void throwExceptionForMissingEqualsChar() {
        try {
            parser.parse(getBytes("8FIX.4.4\u000110=092\u0001"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Unable to parse fix message due to malformed tag value pair", e.getMessage());
        }
    }

}
