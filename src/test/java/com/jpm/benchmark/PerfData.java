package com.jpm.benchmark;

public class PerfData {
    private final String simpleName;
    private final Integer iteration;
    private final Long time;
    private final Long memory;
    private final long totalMemory;

    public PerfData(String simpleName, Integer iteration, long time, long memory, long totalMemory) {
        this.simpleName = simpleName;
        this.iteration = iteration;
        this.time = time;
        this.memory = memory;
        this.totalMemory = totalMemory;
    }

    public static String header(){
        return String.format(" %35s | %10s | %22s  | %39s | %18s | %12s", "Class Name ", "Iteration", "Total Time taken", "Average Time per message", "Memory Usage" ,"Total Memory");
    }

    public  String calculate(){
        return format(simpleName, iteration.toString(), time.toString() , (time * 1000) / iteration.doubleValue(), memory / (1024.0), totalMemory / (1024.0 * 1024.0 *1024.0));
    }

    public static String format(String name, String iteration, String time, Double average, Double memory, Double total) {
        return String.format(" %35s | %10s | %20s ms | %30.2f Micro/msg| %15.2f Kb | %9.2f Gb ", name, iteration, time, average, memory,total);
    }
}
