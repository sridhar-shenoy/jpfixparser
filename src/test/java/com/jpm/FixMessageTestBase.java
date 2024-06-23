package com.jpm;

import com.jpm.api.FixMessageParser;
import com.jpm.fixparser.HighPerformanceLowMemoryFixParser;
import com.jpm.policy.DefaultPolicy;
import org.junit.BeforeClass;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FixMessageTestBase {
    private final Random random = new Random(1000);
    protected static FixMessageParser parser;

    @BeforeClass
    public static void setUp() {
        parser = new HighPerformanceLowMemoryFixParser(DefaultPolicy.getDefaultPolicy());
    }

    protected String toString(byte[] byteValueForTag) {
        return new String(byteValueForTag);
    }

    protected String randomStringOfSize(int size) {
        String characters = getCharacterSet();
        return random.ints(size, 0, characters.length())
                .mapToObj(characters::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    protected byte[] getBytes(String fixMsg) {
        return fixMsg != null ? fixMsg.getBytes() : null;
    }

    private static String getCharacterSet() {
        return IntStream.concat(
                        IntStream.concat(
                                IntStream.rangeClosed('A', 'Z'),
                                IntStream.rangeClosed('a', 'z')),
                        IntStream.rangeClosed('0', '9'))
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }
}
