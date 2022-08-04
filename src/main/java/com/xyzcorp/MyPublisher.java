package com.xyzcorp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class MyPublisher implements Flow.Publisher<Long> {

    private ExecutorService executorService;

    public MyPublisher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super Long> subscriber) {
        subscriber.onSubscribe(new Flow.Subscription() {
            AtomicBoolean done = new AtomicBoolean(false);
            AtomicBoolean started = new AtomicBoolean(false);
            AtomicLong counter = new AtomicLong();
            AtomicLong requests = new AtomicLong();


            @Override
            public void request(long n) {
                requests.addAndGet(n);
                if (!started.get()) {
                    startLoop();
                    started.set(false);
                }
            }

            private void startLoop() {
                executorService.submit(() -> {
                    while (!done.get()) {
                        if (requests.decrementAndGet() >= 0) {
                            subscriber.onNext(counter.incrementAndGet());
                        }
                    }
                    subscriber.onComplete();
                    started.set(false);
                });
            }

            @Override
            public void cancel() {
                done.set(true);
            }
        });
    }
}
