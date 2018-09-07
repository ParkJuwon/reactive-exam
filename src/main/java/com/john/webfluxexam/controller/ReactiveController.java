package com.john.webfluxexam.controller;

import com.john.webfluxexam.service.MyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@EnableAsync
public class ReactiveController {

    static final String URL1 = "http://localhost:8081/service?req={req}";
    static final String URL2 = "http://localhost:8081/service2?req={req}";

    @Autowired
    MyService myService;

    WebClient client = WebClient.create();

    // Mono 하나, Flux 는 Stream
    @GetMapping("/rest")
    public Mono<String> rest(int idx) {
        return client.get().uri(URL1, idx).exchange() // Mono<ClientResponse>
                .flatMap(c -> c.bodyToMono(String.class)) // Mono<String>
                .doOnNext(c -> log.info(c))
                .flatMap(res1 -> client.get().uri(URL2, res1).exchange()) // Mono<ClientResponse>
                .flatMap(c -> c.bodyToMono(String.class)) // Mono<String>
                .doOnNext(c -> log.info(c))
                .flatMap(res2 -> Mono.fromCompletionStage(myService.work3(res2))) // CompletableFuture<String> => Mono<String>
                .doOnNext(c -> log.info(c));

    }


}
