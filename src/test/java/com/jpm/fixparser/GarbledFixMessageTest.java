package com.jpm.fixparser;

import com.jpm.exception.MalformedFixMessageException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static com.jpm.exception.ErrorMessages.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class GarbledFixMessageTest extends FixMessageTestBase {

    public String fixMsg;
    private final String errorString;

    public GarbledFixMessageTest(String fixMsg, String errorString) {
        this.fixMsg = fixMsg;
        this.errorString = errorString;
    }

    @Parameters(name = "FIX Message = {0} ")
    public static List<Object[]> fixMessages() {
        return Arrays.asList(new Object[][]{
                {null, NULL_MESSAGE},
                {"", MISSING_DELIMITER},
                {"$$", MALFORMED_TAG_VALUE_PAIR},
                {"$=$", MALFORMED_TAG_VALUE_PAIR},
                {"1209393249324", MALFORMED_TAG_VALUE_PAIR},
                {"*%^&%$^%$%^$^%$", MALFORMED_TAG_VALUE_PAIR},
        });
    }

    @Test
    public void throwExceptionForGarbledMessage() {
        try {
            parser.parse(getBytes(fixMsg));
        } catch (MalformedFixMessageException e) {
            assertEquals(errorString, e.getMessage());
            return;
        }
        fail("Should have thrown an exception");
    }
}
