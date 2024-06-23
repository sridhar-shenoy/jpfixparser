package com.jpm.benchmark;

import com.jpm.FixMessageTestBase;
import com.jpm.api.FixMessageParser;
import com.jpm.dictionary.DefaultFixDictionary;
import com.jpm.exception.MalformedFixMessageException;
import com.jpm.fixparser.FixMessageParserFactory;
import com.jpm.policy.DefaultPolicy;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParsingPerformanceTest extends FixMessageTestBase {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
    private final int maxMessages = 1000000;
    private final List<Integer> list = Arrays.asList((maxMessages * 5) / 100, (maxMessages * 8) / 100, maxMessages / 10, (maxMessages *3 )/ 10, (maxMessages * 5) / 10, (maxMessages * 8) / 10, maxMessages);
    private final Runtime runtime = Runtime.getRuntime();
    private FixMessageParser artifact1;
    private FixMessageParser artifact2;


    @Before
    public void init() throws MalformedFixMessageException {
        artifact1 = FixMessageParserFactory.getFixMessageParser(DefaultPolicy.getCustomPolicy(10005, 1005, 15000, '\u0001', 1000));
        artifact2 = new TestOnlyParser();
    }

    private ArrayList<byte[]> populateData(byte[] bytes) {
        log("Populating data");
        return (ArrayList<byte[]>) IntStream.rangeClosed(1, maxMessages)
                .mapToObj(i -> {
                    return bytes;
                })
                .collect(Collectors.toList());

    }

    @Ignore("Only for manual runs")
    @Test
    //-- Sample taken from https://cmegroupclientsite.atlassian.net/wiki/spaces/EPICSANDBOX/pages/46472218/CME+STP+FIX+-+Samples+for+BrokerTec+Trades
    public void unrealisticParsingPerformanceTest() throws MalformedFixMessageException, InterruptedException {
        measurePerformance(populateData(generateUnrealisticFixMessage()));
    }

    @Ignore("Only for manual runs")
    @Test
    public void prodLikeMessageParsingPerformanceTest() throws MalformedFixMessageException, InterruptedException {
        String s = "8=FIX.4.4\u00019=193\u000135=AD\u000149=bthomas_trading_brokers_llc\u000156=CMESTPFIX2\u000134=7\u000157=STP\u000152=20160330-21:23:01.588\u0001779=20160330-20:50:48.800\u0001568=d0f298f7-a285-4cd8-9207-9eab0630582a\u0001569=1\u0001263=1\u0001453=1\u0001448=ace4\u000152=7\u0001442=2\u000110=099\u0001";
        measurePerformance(populateData(s.getBytes()));
    }

    private void measurePerformance(ArrayList<byte[]> prodData1) throws MalformedFixMessageException, InterruptedException {
        warmUp(artifact1, prodData1);
        warmUp(artifact2, prodData1);
        coolDown();
        line();
        ArrayList<PerfData> perfData1 = new ArrayList<>();
        log("Executing " + artifact1.getClass().getSimpleName());
        list.forEach(i -> measurePerformance(i, artifact1, perfData1, prodData1));
        coolDown();
        line();
        ArrayList<PerfData> perfData2 = new ArrayList<>();
        log("Executing " + artifact2.getClass().getSimpleName());
        list.forEach(i -> measurePerformance(i, artifact2, perfData2, prodData1));
        report(perfData1,perfData2);
    }

    private void report(ArrayList<PerfData> perfData1, ArrayList<PerfData> perfData2) {
        System.out.println(PerfData.header());
        perfData1.forEach(d -> System.out.println(d.calculate()));
        perfData2.forEach(d -> System.out.println(d.calculate()));
    }

    private static void coolDown() throws InterruptedException {
        System.gc();
        TimeUnit.SECONDS.sleep(5);
    }

    private static void line() {
        System.out.println("____________________________________________________________________");
    }

    private void measurePerformance(Integer i, FixMessageParser artifact, ArrayList<PerfData> perfData, ArrayList<byte[]> prodData1) {
        try {
            long start = System.currentTimeMillis();
            long before = runtime.totalMemory() - runtime.freeMemory();
            exec(artifact, i, prodData1);
            long after = runtime.totalMemory() - runtime.freeMemory();
            long end = System.currentTimeMillis();
            perfData.add(new PerfData(artifact.getClass().getSimpleName(), i, (end - start), (after - before),runtime.totalMemory()));
        }catch (Exception e) {
        }
    }

    public static void log(String message){
        System.out.println(String.format("%18s  : %s", dtf.format(LocalDateTime.now()), message));
    }

    private void warmUp(FixMessageParser artifact, ArrayList<byte[]> data) throws MalformedFixMessageException {
        log("Warming up " + artifact.getClass().getSimpleName());
        exec(artifact, maxMessages/70, data);
    }

    private void exec(FixMessageParser artifact, int mamMessage, ArrayList<byte[]> prodData1) {
        log("Iteration of " + mamMessage);
        IntStream.range(0, mamMessage).forEach(i -> execute(artifact, prodData1.get(i)));
    }

    public static void execute(FixMessageParser parser, byte[] data) {
        try {
            parser.parse(data);
        } catch (MalformedFixMessageException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] generateUnrealisticFixMessage() {
        String s = randomStringOfSize(8);
        DefaultFixDictionary defaultFixDictionary = new DefaultFixDictionary(1006, 1006);
        StringJoiner fixMessage = new StringJoiner("\u0001");
        for (int i = 1; i <= 900; i++) {
            if (!(defaultFixDictionary.isRepeatingGroupBeginTag(i) || defaultFixDictionary.isTagMemberOfRepeatGroup(i, 453))) {
                fixMessage.add(i + "=" + s);
            }
        }
        System.out.println(fixMessage);
        return (fixMessage + "\u0001").getBytes();
    }
}
