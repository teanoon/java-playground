package com.example.experiment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import static java.util.concurrent.ThreadLocalRandom.current;

@SpringBootApplication
public class BasicReactor implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicReactor.class);

    private final Scheduler zipScheduler;
    private final Scheduler heavyScheduler;

    public BasicReactor() {
        zipScheduler = Schedulers.newBoundedElastic(10, 100, "zip");
        heavyScheduler = Schedulers.newBoundedElastic(10, 100, "heavy");
    }

    @Override
    public void run(String... args) throws Exception {
        var ids = Sinks.many().multicast().<Integer>onBackpressureBuffer();
        var identifiers = Sinks.many().multicast().<String>onBackpressureBuffer();
        var aggregations = Sinks.many().multicast().<Integer>onBackpressureBuffer();
        Flux.range(10, 20)
            .doOnNext(index -> feed(index, ids, identifiers, aggregations))
            .doOnComplete(() -> {
                ids.tryEmitComplete();
                identifiers.tryEmitComplete();
                aggregations.tryEmitComplete();
            })
            .subscribe();

        var aggregated = aggregations.asFlux().buffer(10).flatMapSequential(this::heavyLift);

        Flux.zip(ids.asFlux(), identifiers.asFlux(), aggregated)
            .flatMap(data -> Mono
                .fromCallable(() -> String.format("%d %s %d", data.getT1(), data.getT2(), data.getT3()))
                .doOnNext(result -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(20);
                    } catch (InterruptedException ignored) { }
                    LOGGER.info(result);
                })
                .subscribeOn(zipScheduler))
            .doOnNext(result -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(20);
                } catch (InterruptedException ignored) { }
                LOGGER.info("final {}", result);
            })
            .doOnComplete(() -> System.exit(0))
            .subscribe();
    }

    private void feed(int index, Sinks.Many<Integer> ids, Sinks.Many<String> identifiers, Sinks.Many<Integer> aggregations) {
        ids.tryEmitNext(index);
        identifiers.tryEmitNext("id-" + index);
        aggregations.tryEmitNext(index);
    }

    private Flux<Integer> heavyLift(List<Integer> ids) {
        return Flux.fromIterable(ids).map(index -> {
            try {
                TimeUnit.MILLISECONDS.sleep(current().nextInt(5, 15));
                LOGGER.info("lift {}", index);
            } catch (InterruptedException ignored) { }
            return index;
        })
        .subscribeOn(heavyScheduler);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BasicReactor.class, args);
    }

}
