package com.jpm.fixparser;

import com.jpm.exception.MalformedFixMessageException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MalformedFixMessageTest extends FixMessageTestBase {

    @Test
    public void parseSimpleFixMessageWithMissingDelimiterAfterChecksum() {
        try {
            parser.parse(getBytes("8=FIX.4.4\u000110=092"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Unable to parse due to missing delimiter", e.getMessage());
        }
    }

    @Test
    public void parseSimpleFixMessageWithMissingTag() {
        try {
            parser.parse(getBytes("=FIX.4.4\u000110=092\u0001"));
        } catch (MalformedFixMessageException e) {
            assertEquals("Unable to parse fix message due to missing tag", e.getMessage());
        }
    }
}
