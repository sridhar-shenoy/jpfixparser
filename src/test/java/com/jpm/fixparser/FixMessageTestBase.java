package com.jpm.fixparser;

import com.jpm.policy.DefaultPolicy;
import org.junit.BeforeClass;

import java.util.Random;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

public class FixMessageTestBase {
    private final Random random = new Random(1000);
    protected static HighPerformanceLowMemoryFixParser parser;

    @BeforeClass
    public static void setUp() {
        parser = new HighPerformanceLowMemoryFixParser(new DefaultPolicy(100,1000, 10000, '\u0001'));
    }

    protected String toString(byte[] byteValueForTag) {
        return new String(byteValueForTag);
    }

    protected String randomLongString() {
        return generate(() -> "a").limit(random.nextInt(100)).collect(joining());
    }

    protected byte[] getBytes(String fixMsg) {
        return fixMsg != null ? fixMsg.getBytes() : null;
    }

}
