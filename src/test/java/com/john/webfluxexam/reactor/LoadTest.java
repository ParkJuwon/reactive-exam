package com.john.webfluxexam.reactor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTest {
    static AtomicInteger counter = new AtomicInteger(0);
/*
    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(100);

        RestTemplate rt = new RestTemplate();
//        String url = "http://localhost:8080/async";
//        String url = "http://localhost:8080/callable";
        String url = "http://localhost:8080/dr";

        StopWatch main = new StopWatch();

        main.start();

        for (int i = 0; i < 100; i++) {
            es.execute(() -> {
                int idx = counter.addAndGet(1);
                log.info("Thread {}", idx);

                StopWatch sw = new StopWatch();
                sw.start();

                rt.getForObject(url, String.class);
                sw.stop();

                log.info("Elapsed: {} -> {}", idx, sw.getTotalTimeSeconds());

            });
        }

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);
        main.stop();

        log.info("Total: {}", main.getTotalTimeSeconds());



    }
*/

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {

        ExecutorService es = Executors.newFixedThreadPool(100);
        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:8080/rest?idx={idx}";

        // 동기화 기법
        CyclicBarrier barrier = new CyclicBarrier(101);

        StopWatch main = new StopWatch();

        main.start();

        for (int i = 0; i < 100; i++) {
            es.submit(() -> {
                // runnable return 이 없음
                // callable return 필요

                int idx = counter.addAndGet(1);
                barrier.await();
                log.info("Thread {}", idx);

                StopWatch sw = new StopWatch();
                sw.start();

                String res = rt.getForObject(url, String.class, idx);

                sw.stop();

                log.info("Elapsed: {} -> {}, {}", idx, sw.getTotalTimeSeconds(), res);

                return null;
            });
        }

        barrier.await();
        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);
        main.stop();

        log.info("Total: {}", main.getTotalTimeSeconds());






    }

}
