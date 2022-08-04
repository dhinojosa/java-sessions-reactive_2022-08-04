package com.xyzcorp.instructor;

import com.xyzcorp.instructor.MyPublisher;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;

public class MyPublisherTest {

    @Test
    public void testMyPublisher() throws InterruptedException {
        ExecutorService executorService =
            Executors.newFixedThreadPool(10);
        MyPublisher publisher =
            new MyPublisher(executorService);

        publisher.subscribe(new Flow.Subscriber<>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(10);
            }

            @Override
            public void onNext(Long item) {
                System.out.println("S1 (On Next):" + item);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("S1 (On Error)" + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("S1 (On Complete)");
            }
        });

        publisher.subscribe(new Flow.Subscriber<>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(100);
            }

            @Override
            public void onNext(Long item) {
                System.out.println("S2 (On Next):" + item);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("S2 (On Error)" + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("S2 (On Complete)");
            }
        });

        Thread.sleep(10000);
    }
}
