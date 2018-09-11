package com.john.webfluxexam.handler;

import com.john.webfluxexam.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class HelloHandler {
    @Autowired
    MyService myService;

    public Mono<ServerResponse> hello(ServerRequest req) {
        String res = myService.work2(req.pathVariable("name"));
        return ServerResponse.ok().body(fromObject(res));
    }
}
