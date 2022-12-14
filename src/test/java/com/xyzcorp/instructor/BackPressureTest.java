package com.xyzcorp.instructor;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Test;

public class BackPressureTest {
    private Flowable<Long> flowable;
    @Before
    public void startUp() {
        flowable = Flowable.create(emitter -> {
            long i = 0;
            while (true) {
                emitter.onNext(i++);
            }
        }, BackpressureStrategy.BUFFER);
    }

    @Test
    public void testBackpressure() {
        flowable
            .observeOn(Schedulers.newThread())
//            .onBackpressureDrop()
            .subscribe(i -> {
                Thread.sleep(5);
                System.out.println(i);
            }, Throwable::printStackTrace);
    }
}
