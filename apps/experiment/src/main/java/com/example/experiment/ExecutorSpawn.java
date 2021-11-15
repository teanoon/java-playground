package com.example.experiment;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ExecutorSpawn {

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(
            5, 10, 0, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(5),
            Executors.defaultThreadFactory());
        var counter = new AtomicInteger();
        IntStream.range(0, Integer.MAX_VALUE).forEach(index -> executorService.execute(() -> {
            System.out.printf("%d done.%n", counter.incrementAndGet());
        }));
        executorService.shutdown();
    }

}
