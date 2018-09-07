package com.john.webfluxexam;

import com.john.webfluxexam.service.MyService;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;

@SpringBootApplication
@Slf4j
//@EnableAsync
public class WebfluxExamApplication {

    @Bean
    @Profile("netty")
    NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        return new NettyReactiveWebServerFactory();
    }

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

        RestTemplate rt = new RestTemplate();
        AsyncRestTemplate art = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

        @GetMapping("/async")
        public String async() throws InterruptedException {
            log.info("async");
            Thread.sleep(2000);
            return "hello";
        }

        //        @GetMapping("/rest")
        //        public String rest(int idx) throws InterruptedException {
        //            String res = rt.getForObject("http://localhost:8081/service?req={req}",
        //                    String.class,"hello" + idx);
        //            return res;
        //        }

        //        @GetMapping("/rest")
        //        public ListenableFuture<ResponseEntity<String>> rest(int idx) {
        //
        //            return art.getForEntity("http://localhost:8081/service?req={req}",
        //                    String.class,"hello" + idx);
        //        }


        //        // callback을 받아 비동기로 처리
        //        @GetMapping("/rest")
        //        public DeferredResult<String> rest(int idx) {
        //            DeferredResult<String> dr = new DeferredResult<>();
        //
        //            ListenableFuture<ResponseEntity<String>> f1 = art.getForEntity("http://localhost:8081/service?req={req}",
        //                    String.class,"hello" + idx);
        //            f1.addCallback(s->{
        //                dr.setResult(s.getBody() + "/work");
        //            }, e-> {
        //                dr.setErrorResult(e.getMessage());
        //            });
        //
        //            return dr;
        //        }

        @Autowired
        MyService myService;

        @GetMapping("/callable")
        public Callable<String> callable() throws InterruptedException {
            log.info("callable");
            return () -> {
                log.info("async");
                Thread.sleep(2000);
                return "hello";
            };
        }

        Queue<DeferredResult<String>> results = new ConcurrentLinkedDeque<>();

        @GetMapping("/dr")
        public DeferredResult<String> deferredResult() {
            log.info("dr");
            DeferredResult<String> dr = new DeferredResult<>(600000L);
            results.add(dr);
            return dr;
        }

        @GetMapping("/dr/count")
        public String drCount() {
            return String.valueOf(results.size());
        }

        @GetMapping("/dr/event")
        public String drEvent(String msg) {
            for (DeferredResult<String> dr : results) {
                dr.setResult("Hello " + msg);
                results.remove(dr);
            }

            return "OK";
        }

//        @GetMapping("/emitter")
//        public ResponseBodyEmitter emitter() {
//            ResponseBodyEmitter emitter = new ResponseBodyEmitter();
//            Executors.newSingleThreadExecutor().submit(() -> {
//                try {
//                    for (int i = 1; i <= 50; i++) {
//                        emitter.send("<p>stream" + i + "</p>");
//                        Thread.sleep(100);
//                    }
//                } catch (Exception e) {
//                }
//            });
//
//            return emitter;
//        }
    }


//    public static void main(String[] args) {
//
//        //        try (ConfigurableApplicationContext c = SpringApplication.run(WebfluxExamApplication.class, args)) {}
//        SpringApplication.run(WebfluxExamApplication.class, args);
//    }


    public static void main(String[] args) {
        System.setProperty("reactor.ipc.netty.workerCount", "1");
        System.setProperty("reactor.ipc.netty.pool.maxConnections", "2000");
        SpringApplication.run(WebfluxExamApplication.class, args);
    }
}
