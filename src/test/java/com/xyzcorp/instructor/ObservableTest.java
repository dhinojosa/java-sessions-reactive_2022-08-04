package com.xyzcorp.instructor;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ObservableTest {

    @Test
    public void testBasicObservable() {
        Observable.just(1, 2, 4, 10)
                  .subscribe(System.out::println);
    }

    @Test
    public void testObservableCreate() {

        Observable<Long> longObservable = Observable.<Long>create(emitter -> {
            emitter.onNext(10L);
            emitter.onNext(20L);
            emitter.onComplete();
        });

        longObservable.subscribe(System.out::println,
            Throwable::printStackTrace,
            () -> System.out.println("Complete"));
    }

    @Test
    public void testObservableCreateAndMap() {

        Observable<Long> longObservable = Observable
            .<Long>create(emitter -> {
                emitter.onNext(10L);
                emitter.onNext(20L);
                emitter.onComplete();
            });

        Disposable disposable = longObservable
            .map(i -> i * 3)
            .map(String::valueOf)
            .subscribe(System.out::println,
                Throwable::printStackTrace,
                () -> System.out.println("Complete"));
    }

    @Test
    public void testObservableCreateAndMapAndFilterForked() {

        //I want forking!
        // On one fork, keep the mapping (*3 + valueOf)
        // On another fork, filter all the odd numbers
        // Each fork should have its own subscribe

        Observable<Long> originalObservable = Observable
            .<Long>create(emitter -> {
                emitter.onNext(10L);
                emitter.onNext(20L);
                emitter.onNext(23L);
                emitter.onNext(35L);
                emitter.onComplete();
            });

        Disposable disposable = originalObservable
            .map(i -> i * 3)
            .map(String::valueOf)
            .subscribe(
                x -> debug("S1 (On Next)", x),
                t -> debug("S1 (Error)", t.getMessage()),
                () -> debug("S1 (Complete)", ""));

        Disposable disposable1 =
            originalObservable
                .filter(x -> x % 2 != 0)
                .subscribe(
                    x -> debug("S2 (On Next)", x),
                    t -> debug("S2 (Error)", t.getMessage()),
                    () -> debug("S2 (Complete)", ""));
    }

    public <A> void debug(String label, A a) {
        System.out.printf("%s: %s - %s\n", label, a,
            Thread.currentThread().getName());
    }

    @Test
    public void testInterval() throws InterruptedException {
        //`Disposable//
        Disposable subscribe = Observable
            .interval(1, TimeUnit.SECONDS)
            .doOnNext(x -> debug("in doOnNext: ", x))
            .subscribe(System.out::println);
        Thread.sleep(4000);
        subscribe.dispose();
        System.out.println("The flow should close");
        Thread.sleep(5000);
    }

    @Test
    public void testIntervalShutOffFlowFromWithin() throws InterruptedException {
        //`Disposable//
        Observable
            .interval(1, TimeUnit.SECONDS)
            .doOnNext(x -> debug("in doOnNext: ", x))
            .subscribe(new Observer<Long>() {
                private Disposable d;

                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    this.d = d;
                }

                @Override
                public void onNext(@NonNull Long aLong) {
                    System.out.println(aLong);
                    if (aLong == 5) d.dispose();
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {
                    System.out.println("OnComplete");
                }
            });
        Thread.sleep(10000);
    }
}
