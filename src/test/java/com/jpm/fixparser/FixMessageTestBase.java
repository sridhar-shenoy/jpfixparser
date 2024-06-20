package com.jpm.fixparser;

import org.junit.BeforeClass;

import java.util.Random;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

public class FixMessageTestBase {
    private final Random random = new Random(1000);
    protected static HighPerformanceLowMemoryFixParser parser;

    @BeforeClass
    public static void setUp() {
        parser = new HighPerformanceLowMemoryFixParser(1000, 1000, '\u0001');
    }

    protected String toString(byte[] byteValueForTag) {
        return new String(byteValueForTag);
    }

    protected String randomLongString() {
        return generate(() -> "a").limit(1000 + random.nextInt(10000)).collect(joining());
    }

    protected byte[] getBytes(String fixMsg) {
        return fixMsg.getBytes();
    }

}
