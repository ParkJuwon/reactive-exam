package com.john.webfluxexam.reactor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class CFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //        CompletableFuture<Integer> f = CompletableFuture.completedFuture(1);
//        CompletableFuture<Integer> f = new CompletableFuture<>();
////        f.complete(2);
//        f.completeExceptionally(new RuntimeException());
//        System.out.println(f.get());

        ExecutorService es = Executors.newFixedThreadPool(10);

        CompletableFuture
                .supplyAsync(() -> {
                    log.info("supplyAsync");
//                    if( 1 == 1) throw new RuntimeException();
                    return 1;
                }, es)
//                .thenApply(s -> {
//                    log.info("thenApply {}", s);
//                    return s + 1;
//                })
                .thenCompose(s -> {
                    log.info("thenApply {}", s);
                    return CompletableFuture.completedFuture(s + 1);
                })
//                .thenApply(s2 -> {
//                    log.info("thenApply {}", s2);
//                    return s2 * 3;
//                })
                .thenApplyAsync(s2 -> {
                    log.info("thenApply {}", s2);
                    return s2 * 3;
                }, es)
                .exceptionally(e -> -10)
                .thenAcceptAsync(s3 -> {
                    log.info("thenAccept {}", s3);
                }, es);
//                CompletableFuture
//                .runAsync(()-> log.info("runAsync"))
//                .thenRun(() -> log.info("thenRun"))
//                .thenRun(() -> log.info("thenRun"));

        log.info("exit");

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }
}
