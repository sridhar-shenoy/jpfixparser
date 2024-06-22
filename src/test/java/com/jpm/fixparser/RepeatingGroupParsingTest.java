package com.jpm.fixparser;

import com.jpm.exception.MalformedFixMessageException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RepeatingGroupParsingTest extends FixMessageTestBase {
    @Test
    public void parseSimpleFixMessageForStringValues() throws MalformedFixMessageException {
        parser.parse(getBytes("8=FIX.4.2\u00019=178\u000135=D\u000134=4\u000149=CLIENT12\u000152=20130615-19:30:00\u000156=BROKER12\u0001" +
                "453=2\u0001" +
                "448=Client1\u0001447=5\u0001452=6\u0001" +
                "448=Client2\u0001447=D\u0001452=7\u0001" +
                "10=037\u0001"));
        assertEquals("FIX.4.2", parser.getStringValueForTag(8));
        assertEquals("Client1", parser.getStringValueForTag(448,453,0,0));
        assertEquals("Client2", parser.getStringValueForTag(448,453,1,0));
        assertEquals("5", parser.getStringValueForTag(447,453,0,0));
        assertEquals("D", parser.getStringValueForTag(447,453,1,0));
        assertEquals("6", parser.getStringValueForTag(452,453,0,0));
        assertEquals("7", parser.getStringValueForTag(452,453,1,0));
    }
}
