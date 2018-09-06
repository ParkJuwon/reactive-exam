package com.john.webfluxexam.reactor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //        CompletableFuture<Integer> f = CompletableFuture.completedFuture(1);
        CompletableFuture<Integer> f = new CompletableFuture<>();
//        f.complete(2);
        f.completeExceptionally(new RuntimeException());
        System.out.println(f.get());
    }
}
