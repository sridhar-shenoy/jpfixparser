package com.jpm.fixparser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleFixMessageSanityTest extends FixMessageTestBase {

    @Test
    public void parseSimpleFixMessageForStringValues() {
        parser.parse(getBytes("8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000110=092\u0001"));

        assertEquals("FIX.4.4", parser.getStringValueForTag(8));
        assertEquals("148", parser.getStringValueForTag(9));
        assertEquals("D", parser.getStringValueForTag(35));
        assertEquals("092", parser.getStringValueForTag(10));
    }

    @Test
    public void parseSimpleFixMessageForByteValues() {
        parser.parse(getBytes("8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000110=092\u0001"));

        assertEquals("FIX.4.4", toString(parser.getByteValueForTag(8)));
        assertEquals("148", toString(parser.getByteValueForTag(9)));
        assertEquals("D", toString(parser.getByteValueForTag(35)));
        assertEquals("092", toString(parser.getByteValueForTag(10)));
    }

    @Test
    public void parseSimpleFixMessageWithDifferentDelimiterForByteValues() {
        HighPerformanceLowMemoryFixParser pipeDelimitedParser = new HighPerformanceLowMemoryFixParser(1000,1000,'|');
        pipeDelimitedParser.parse(getBytes("8=FIX.4.4|9=148|35=D|34=1080|10=092|"));

        assertEquals("FIX.4.4", toString(pipeDelimitedParser.getByteValueForTag(8)));
        assertEquals("148", toString(pipeDelimitedParser.getByteValueForTag(9)));
        assertEquals("D", toString(pipeDelimitedParser.getByteValueForTag(35)));
        assertEquals("092", toString(pipeDelimitedParser.getByteValueForTag(10)));
    }

    @Test
    public void parseSimpleFixMessageWithDifferentDelimiterForStringValues() {
        HighPerformanceLowMemoryFixParser pipeDelimitedParser = new HighPerformanceLowMemoryFixParser(1000,1000,'|');
        pipeDelimitedParser.parse(getBytes("8=FIX.4.4|9=148|35=D|34=1080|10=092|"));

        assertEquals("FIX.4.4", pipeDelimitedParser.getStringValueForTag(8));
        assertEquals("148", pipeDelimitedParser.getStringValueForTag(9));
        assertEquals("D", pipeDelimitedParser.getStringValueForTag(35));
        assertEquals("092", pipeDelimitedParser.getStringValueForTag(10));
    }

    /*
       Randomized tests for Text parsing
     */
    @Test
    public void parseExtraLongTextMessage() {
        String randomLongString = randomLongString();
        parser.parse(getBytes("8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000158=" + randomLongString + "\u000110=092\u0001"));
        assertEquals(randomLongString, parser.getStringValueForTag(58));
    }


}
