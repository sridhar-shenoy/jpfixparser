package com.jpm.fixparser;

import com.jpm.FixMessageTestBase;
import com.jpm.api.FixMessageParser;
import com.jpm.benchmark.TestOnlyParser;
import com.jpm.exception.MalformedFixMessageException;
import com.jpm.policy.DefaultPolicy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/*
   Please note

   FixMessageParserFactory.getFixMessageParser() is the Production parser
   ClassicFixMessageParser is only added here to illustrated they are functionally same

 */
@RunWith(Parameterized.class)
public class ParsingSimpleFixMessageTest extends FixMessageTestBase {

    private final FixMessageParser fixMessageParser;

    public ParsingSimpleFixMessageTest(FixMessageParser fixMessageParser) {
        this.fixMessageParser = fixMessageParser;
    }

    @Parameterized.Parameters(name = "Parser In Use = {0} ")
    public static List<Object[]> fixMessages() {
        return Arrays.asList(new Object[][]{
                {FixMessageParserFactory.getFixMessageParser()},
                {new TestOnlyParser()} //-- ONLY FOR BENCHMARK  TESTING
        });
    }

    @Test
    public void parseSimpleFixMessageForStringValues() throws MalformedFixMessageException {
        fixMessageParser.parse(getBytes("8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000110=092\u0001"));

        assertEquals("FIX.4.4", fixMessageParser.getStringValueForTag(8));
        assertEquals("148", fixMessageParser.getStringValueForTag(9));
        assertEquals("D", fixMessageParser.getStringValueForTag(35));
        assertEquals("092", fixMessageParser.getStringValueForTag(10));
    }

    @Test
    public void parseSimpleFixMessageForByteValues() throws MalformedFixMessageException {
        fixMessageParser.parse(getBytes("8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000110=092\u0001"));

        assertEquals("FIX.4.4", toString(fixMessageParser.getByteValueForTag(8)));
        assertEquals("148", toString(fixMessageParser.getByteValueForTag(9)));
        assertEquals("D", toString(fixMessageParser.getByteValueForTag(35)));
        assertEquals("092", toString(fixMessageParser.getByteValueForTag(10)));
    }

    @Test
    public void parseSimpleFixMessageWithDifferentDelimiterForByteValues() throws MalformedFixMessageException {
        HighPerformanceLowMemoryFixParser pipeDelimitedParser = new HighPerformanceLowMemoryFixParser(DefaultPolicy.getCustomPolicy(1000,1000,1000,'|',100));
        pipeDelimitedParser.parse(getBytes("8=FIX.4.4|9=148|35=D|34=1080|10=092|"));

        assertEquals("FIX.4.4", toString(pipeDelimitedParser.getByteValueForTag(8)));
        assertEquals("148", toString(pipeDelimitedParser.getByteValueForTag(9)));
        assertEquals("D", toString(pipeDelimitedParser.getByteValueForTag(35)));
        assertEquals("092", toString(pipeDelimitedParser.getByteValueForTag(10)));
    }

    @Test
    public void parseSimpleFixMessageWithDifferentDelimiterForStringValues() throws MalformedFixMessageException {
        HighPerformanceLowMemoryFixParser pipeDelimitedParser = new HighPerformanceLowMemoryFixParser(DefaultPolicy.getCustomPolicy(1000,1000,1000,'|',100));
        pipeDelimitedParser.parse(getBytes("8=FIX.4.4|9=148|35=D|34=1080|10=092|"));

        assertEquals("FIX.4.4", pipeDelimitedParser.getStringValueForTag(8));
        assertEquals("148", pipeDelimitedParser.getStringValueForTag(9));
        assertEquals("D", pipeDelimitedParser.getStringValueForTag(35));
        assertEquals("092", pipeDelimitedParser.getStringValueForTag(10));
    }

    @Test
    public void parserMustNotStoreStatesFromPreviousRun() throws MalformedFixMessageException {
        fixMessageParser.parse(getBytes("8=FIX.4.4\u000121=1\u0001"));
        fixMessageParser.parse(getBytes("8=FIX.4.4\u000122=2\u0001"));

        assertNull(fixMessageParser.getByteValueForTag(21));
    }

    @Test
    public void multipleParsingOfFixMessageReturnsLatest() throws MalformedFixMessageException {
        fixMessageParser.parse(getBytes("8=FIX.4.4\u000121=1\u0001"));
        fixMessageParser.parse(getBytes("8=FIX.4.4\u000122=2\u0001"));
        fixMessageParser.parse(getBytes("8=FIX.4.4\u000122=3\u0001"));
        fixMessageParser.parse(getBytes("8=FIX.4.4\u000122=4\u0001"));
        fixMessageParser.parse(getBytes("8=FIX.4.4\u000122=5\u000110=001\u0001"));
        fixMessageParser.parse(getBytes("8=FIX.4.4\u000122=6\u0001"));

        assertEquals("6", toString(fixMessageParser.getByteValueForTag(22)));
        assertNull(fixMessageParser.getByteValueForTag(10));
    }

    @Test
    public void inputFixMessageCaneBeMutatedAfterParsing() throws MalformedFixMessageException {
        byte[] bytes = getBytes("8=FIX.4.4\u000121=1\u0001");
        fixMessageParser.parse(bytes);
        bytes[0] = '\u0001';
        bytes[1] = '\u0001';
        assertEquals("FIX.4.4", toString(fixMessageParser.getByteValueForTag(8)));
        assertNull(fixMessageParser.getByteValueForTag(10));
    }


    //-- randomized tests
    @Test
    public void parseExtraLongTextMessage() throws MalformedFixMessageException {
        String randomLongString = randomStringOfSize(100);
        fixMessageParser.parse(getBytes("8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000158=" + randomLongString + "\u000110=092\u0001"));
        assertEquals(randomLongString, fixMessageParser.getStringValueForTag(58));
    }

    @Test
    public void parseTagAndCopyToClientsProvidedArrayWithExactLength() throws MalformedFixMessageException {
        String randomString = randomStringOfSize(10);
        fixMessageParser.parse(getBytes("8=FIX.4.4\u000158=" + randomString + "\u0001"));

        byte[] clientsArray = new byte[10];
        int actualLength = fixMessageParser.copyByteValuesToArray(58, clientsArray);
        assertEquals(randomString.length(),actualLength);
        assertEquals(randomString,new String(clientsArray));
    }

    @Test
    public void parseTagAndCopyToClientsProvidedArrayWithSmallerLength() throws MalformedFixMessageException {
        String s = randomStringOfSize(10);
        fixMessageParser.parse(getBytes("8=FIX.4.4\u000158=" + s + "\u0001"));

        byte[] clientsArray = new byte[5];
        int result = fixMessageParser.copyByteValuesToArray(58, clientsArray);
        assertEquals(-1,result);
    }
}
