package com.xyzcorp.instructor;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
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
        Observable<Long> longObservable = Observable.<Long>create(emitter -> {
            emitter.onNext(10L);
            emitter.onNext(20L);
            emitter.onComplete();
        });

        Observable<String> mappedObservable =
            longObservable.map(i -> i * 3).map(String::valueOf);

        mappedObservable.subscribe(System.out::println,
            Throwable::printStackTrace,
            () -> System.out.println("Complete"));
    }

}
