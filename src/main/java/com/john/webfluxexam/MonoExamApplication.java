package com.john.webfluxexam;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@Slf4j
public class MonoExamApplication {

    @GetMapping("/")
    Mono<String> hello() {
        log.info("pos1");
        String msg = generateHello();
        // Publisher -> (Publisher) -> (Publisher) -> Subscriber
//        Mono m = Mono.fromSupplier(() -> generateHello()).doOnNext(c -> log.info(c)).log();
        Mono<String> m = Mono.just(msg).doOnNext(c -> log.info(c)).log();
//        m.subscribe(); // Mono, Flux 는 하나 이상의 subscribe 를 가질 수 있다.
        String msg2 = m.block();
        log.info("pos2: "+ msg2);
        return m;
    }

    private String generateHello() {
        log.info("method generateHello()");
        return "Hello Mono";
    }

    public static void main(String[] args) {
        SpringApplication.run(MonoExamApplication.class, args);
    }
}
