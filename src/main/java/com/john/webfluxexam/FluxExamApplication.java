package com.john.webfluxexam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

@SpringBootApplication
@RestController
@Slf4j
public class FluxExamApplication {

    @GetMapping("/event/{id}")
    Mono<List<Event>> event(@PathVariable long id) {
        List<Event> list = Arrays.asList(new Event(1L, "event1"), new Event(2L, "event2"));
        return Mono.just(list);
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events() {
        // step 1
//        return Flux.just(new Event(1L, "event1"), new Event(2L, "event2"));

        // step 2
//        List<Event> list = Arrays.asList(new Event(1L, "event1"), new Event(2L, "event2"));
//        return Flux.fromIterable(list);


        // step 3
//        Stream<Event> s = Stream.generate(() -> new Event(System.currentTimeMillis(), "value")).limit(100);
//        return Flux.fromStream(s)
//                .delayElements(Duration.ofSeconds(1))
//                .take(10);

        // step 4
//        return Flux
//                .<Event>generate(sink -> sink.next(new Event(System.currentTimeMillis(), "value")))
//                .delayElements(Duration.ofSeconds(1))
//                .take(10);


        // step 5
//        return Flux
//                .<Event, Long>generate(() -> 1L, (id, sink) -> {
//                    sink.next(new Event(id, "value" + id));
//                    return id + 1;
//                })
//                .delayElements(Duration.ofSeconds(1))
//                .take(10);



        // step 6 flux merge
//        Flux<Event> es = Flux.<Event, Long>generate(() -> 1L, (id, sink) ->{
//            sink.next(new Event(id, "value" + id));
//                    return id + 1;
//        });
//        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
//        return Flux.zip(es, interval).map(tu -> tu.getT1()).take(10);


        // step 7
        Flux<String> es = Flux.generate(sink -> sink.next("value"));
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
        return Flux.zip(es, interval).map(tu -> new Event(tu.getT2(), tu.getT1())).take(10);
    }

    @Data
    @AllArgsConstructor
    public static class Event {
        long id;
        String value;
    }

    public static void main(String[] args) {
        SpringApplication.run(FluxExamApplication.class, args);
    }
}
