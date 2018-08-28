package com.john.webfluxexam.reactor;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;


// 비동기 작업 대표
// Future
// Callback

@Slf4j
public class FutureEx {

    interface SuccessCallback {
        void onSuccess(String result);
    }

    interface ExceptionCallback {
        void onError(Throwable t);
    }

    public static class CallbackFutureTask extends FutureTask<String> {
        SuccessCallback sc;
        ExceptionCallback ec;

        public CallbackFutureTask(Callable<String> callable, SuccessCallback sc, ExceptionCallback ec) {
            super(callable);
            this.sc = Objects.requireNonNull(sc);
            this.ec = Objects.requireNonNull(ec);
        }

        @Override
        protected void done() {
            try {
                sc.onSuccess(get());
            } catch (InterruptedException e) {
//                e.printStackTrace();
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
//                e.printStackTrace();
                ec.onError(e.getCause());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();

        CallbackFutureTask f = new CallbackFutureTask(()->{
            Thread.sleep(2000);
            if(1 == 1) throw new RuntimeException("Async ERROR!!!");
            log.info("Async");
            return "Hello";
        },
//                res -> {
//            System.out.println(res);
//        }
                s-> System.out.println("Result : "+ s),
                e-> System.out.println("Error : " + e.getMessage())
        );


        /*
        // FutureTask: future, submit 이 하나의 obj
        FutureTask<String> f = new FutureTask<String>(() -> { // return 이 필요할 시 submit

            Thread.sleep(2000);

            log.info("Async");
            return "Hello";
        }) {
            @Override
            protected void done() {
                try {
                    System.out.println(get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }; // 익명 클래스

        */

        es.execute(f);


        /*
        //                es.execute(() -> {  // execute 는 runnable 이라 return 이 없음
        //        Future<String> f = es.submit(() -> { // return 이 필요할 시 submit
        //
        //            Thread.sleep(2000);
        //
        //            log.info("Async");
        //            return "Hello";
        //        });

        System.out.println(f.isDone());
        Thread.sleep(2100);
        log.info("Exit");
        System.out.println(f.isDone());
        System.out.println(f.get()); // blocking
        */

        es.shutdown();
    }
}
