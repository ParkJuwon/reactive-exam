package com.john.webfluxexam;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@SpringBootApplication
@Slf4j
@RestController
public class SpringCampWebFluxApplication {

    @GetMapping("/hello/{name}")
    String hello(@PathVariable String name) {
        return "Hello" + name;
    }

//    @GetMapping("/hello2/{name}")
    HandlerFunction helloHandler = req -> {
        String name = req.pathVariable("name");
        Mono<String> result = Mono.just("Hello" + name);
        Mono<ServerResponse> res = ServerResponse.ok().body(result, String.class);
        return res;
    };


    public static void main(String[] args) {
        SpringApplication.run(SpringCampWebFluxApplication.class, args);
    }
}
