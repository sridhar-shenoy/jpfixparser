package com.jpm.fixparser;

import com.jpm.exception.MalformedFixMessageException;
import org.junit.Test;

public class MalformedFixMessageTest extends FixMessageTestBase{

    @Test(expected = MalformedFixMessageException.class)
    public void parseSimpleFixMessageWithMissingDelimiterAfterChecksum() {
        parser.parse(getBytes("8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000110=092"));
    }
}
