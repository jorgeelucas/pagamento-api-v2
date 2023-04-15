package com.pagamento.model;

import reactor.core.publisher.Mono;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // Mono.fromCallable() : T item
        // Mono.defer() : Mono.just()

        //Mono<Long> mono = Mono.just(System.currentTimeMillis());
        Mono<Long> mono = Mono.defer(() -> Mono.just(System.currentTimeMillis()));

        mono.subscribe(System.out::println);
        Thread.sleep(200);
        mono.subscribe(System.out::println);
        Thread.sleep(200);
        mono.subscribe(System.out::println);
        Thread.sleep(200);
        mono.subscribe(System.out::println);

    }
}
