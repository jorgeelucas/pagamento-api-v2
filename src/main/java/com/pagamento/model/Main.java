package com.pagamento.model;

import reactor.core.publisher.Mono;

import java.time.Instant;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Mono<Long> mono = Mono.fromCallable(() -> Instant.now().toEpochMilli());
        //Mono<Long> mono = Mono.defer(() -> Mono.just(Instant.now().toEpochMilli()));

        mono.subscribe(System.out::println);
        Thread.sleep(200);
        mono.subscribe(System.out::println);
        Thread.sleep(200);
        mono.subscribe(System.out::println);
        Thread.sleep(200);
        mono.subscribe(System.out::println);
    }
}
