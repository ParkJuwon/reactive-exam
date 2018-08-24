package com.john.webfluxexam.reactor;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FluxScEx {

    public static void main(String[] args) throws InterruptedException {
        //        Flux.range(1, 10)
        //                .publishOn(Schedulers.newSingle("pub"))
        //                .log()
        //                .subscribeOn(Schedulers.newSingle("sub"))
        //                .subscribe(System.out::println);
        //
        //        System.out.println("exit");



        ////////////////////////////////////////////////////////////////////////////////////////////////////


        //        Flux.interval(Duration.ofMillis(500)) // 주기를 가지고 계속 쏴줌
        //        .subscribe(s -> log.debug("onNext: {}", s));
        //
        //        log.debug("exit");
        //        TimeUnit.SECONDS.sleep(5);


        ////////////////////////////////////////////////////////////////////////////////////////////////////

        //        Executors.newSingleThreadExecutor().execute(() -> {
        //            try {
        //                TimeUnit.SECONDS.sleep(2);
        //            } catch (InterruptedException e) {}
        //
        //            System.out.println("Hello");
        //        });
        //
        //        System.out.println("exit");


        ////////////////////////////////////////////////////////////////////////////////////////////////////


        Flux.interval(Duration.ofMillis(200)) // 주기를 가지고 계속 쏴줌 유저 쓰레드가 아니라 데몬 쓰레드를 만든다. 그래서 메인이 종료되면 실행되지 않음 ㅠ
                .take(10)
                .subscribe(s -> log.debug("onNext: {}", s));

        log.debug("exit");
        TimeUnit.SECONDS.sleep(10);
    }
}
