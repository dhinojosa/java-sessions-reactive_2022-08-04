package com.xyzcorp.instructor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.function.Consumer;

public class FluxTest {

    @Test
    public void testFlux() {
        Flux<Long> longFlux = Flux.<Long>create(emitter -> {
            emitter.next(10L);
            emitter.next(13L);
            emitter.next(15L);
        });

        longFlux
            .publishOn(Schedulers.single())
            .subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) {
                    System.out.println(aLong);
                }
            });
    }
}
