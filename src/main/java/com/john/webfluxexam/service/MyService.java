package com.john.webfluxexam.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
@Slf4j
public class MyService {
    @Async(value = "tp") // 요청시 계속 쓰레드를 생성해서 절대로 실무에서 사용하지 말아야함
    //        public Future<String> hello() throws InterruptedException {
    //        public CompletableFuture<String> hello() throws InterruptedException {
    public ListenableFuture<String> hello() throws InterruptedException {

        log.info("hello()");
        Thread.sleep(2000);
        //            return "Hello";
        return new AsyncResult<>("Hello");
    }

    @Async
    public ListenableFuture<String> work(String req) {
        return new AsyncResult<>(req + "/asyncwork");
    }


    public String work2(String req) {
        return req + "/asyncwork";
    }
}
