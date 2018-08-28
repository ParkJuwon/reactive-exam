package com.john.webfluxexam;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@SpringBootApplication
@Slf4j
@EnableAsync
public class WebfluxExamApplication {

    @RestController
    public static class Controller {
        @RequestMapping("/hello")
        public Publisher<String> hello(String name) {
            return s -> s.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    s.onNext("Hello! " + name);
                    s.onComplete();
                }

                @Override
                public void cancel() {

                }
            });
        }

    }

    @Component
    public static class MyService {
        @Async(value="tp") // 요청시 계속 쓰레드를 생성해서 절대로 실무에서 사용하지 말아야함
        //        public Future<String> hello() throws InterruptedException {
        //        public CompletableFuture<String> hello() throws InterruptedException {
        public ListenableFuture<String> hello() throws InterruptedException {

            log.info("hello()");
            Thread.sleep(2000);
            //            return "Hello";
            return new AsyncResult<>("Hello");
        }
    }

    /*
    // Executor
    // ExecutorService
    // ThreadPoolTaskExecutor : 기본적
    // 해당 Bean이 등록되어 있으면 Async시 해당 Executor 사용
   @Bean
    ThreadPoolTaskExecutor tp() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(10); // thread 수를 기본적으로 만듬
        te.setMaxPoolSize(100);
        te.setQueueCapacity(200);
        te.setThreadNamePrefix("myThread");
        te.initialize();

        // core -> queue -> max
        return te;
    }
    */

    public static void main(String[] args) {

        try (ConfigurableApplicationContext c = SpringApplication.run(WebfluxExamApplication.class, args)) {}
    }

    @Autowired
    MyService myService;
/*
    @Bean
    ApplicationRunner run() {
        return args -> {
            log.info("run()");
            ListenableFuture<String> f = myService.hello();
            f.addCallback(s -> System.out.println(s), e -> System.out.println(e.getMessage()));
            log.info("exit");
            //            log.info("exit: " + f.isDone());
            //            log.info("exit: " + f.get());


        };
    }
    */

    @RestController
    public static class MyController {

        @GetMapping("/async")
        public String async() {

        }
    }
}
