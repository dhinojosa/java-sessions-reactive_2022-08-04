package com.xyzcorp.instructor;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import org.junit.Test;

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
                x -> System.out.format("S1 (On Next): %s\n", x),
                t -> System.out.format("S1 (Error): %s\n", t.getMessage()),
                () -> System.out.println("S1 (Complete)"));

        Disposable disposable1 =
            originalObservable
                .filter(x -> x % 2 != 0)
                .subscribe(
                    x -> System.out.format("S2 (On Next): %d\n", x),
                    t -> System.out.format("S2 (Error): %s\n", t.getMessage()),
                    () -> System.out.println("S2 (Complete"));
    }

}
