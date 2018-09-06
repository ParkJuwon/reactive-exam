package com.john.webfluxexam;

import com.john.webfluxexam.service.MyService;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;


@RestController
public class MyTestController {

    @Autowired
    MyService myService;

    AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

    static final String URL1 = "http://localhost:8081/service?req={req}";
    static final String URL2 = "http://localhost:8081/service2?req={req}";

    // callback을 받아 비동기로 처리
    @GetMapping("/rest")
    public DeferredResult<String> rest(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();

        toCf(rt.getForEntity(URL1, String.class, "hello" + idx))
                .thenCompose(s -> {
                    if(1 == 1) throw new RuntimeException("ERROR");
                    return toCf(rt.getForEntity(URL2, String.class, s.getBody()));
                })
//                .thenCompose(s2 -> toCf(myService.work(s2.getBody())))
                .thenApplyAsync(s2 -> myService.work2(s2.getBody()))
                .thenAccept(s3 -> dr.setResult(s3))
                .exceptionally(e -> {
                    dr.setErrorResult(e.getMessage());
                    return (Void) null;
                });

//                ListenableFuture<ResponseEntity<String>> f1 = rt.getForEntity("http://localhost:8081/service?req={req}",
//                        String.class, "hello" + idx);
//                f1.addCallback(s -> {
//                    ListenableFuture<ResponseEntity<String>> f2 = rt.getForEntity("http://localhost:8081/service2?req={req}",
//                            String.class, s.getBody());
//                    f2.addCallback(s2 -> {
//                                ListenableFuture<String> f3 = myService.work(s2.getBody());
//                                f3.addCallback(s3 -> {
//                                    dr.setResult(s3);
//                                }, e -> {
//                                    dr.setErrorResult(e.getMessage());
//                                });
//
//                            }, e -> {
//                                dr.setErrorResult(e.getMessage());
//                            }
//                    );
//                }, e -> {
//                    dr.setErrorResult(e.getMessage());
//                });


//        Completion
//                .from(rt.getForEntity(URL1, String.class, "hello" + idx))
//                .andApply(s -> rt.getForEntity(URL2, String.class, s.getBody()))
//                .andApply(s -> myService.work(s.getBody()))
//                .andError(e -> dr.setErrorResult(e.toString()))
//                .andAccept(s -> dr.setResult(s));


        return dr;
    }

    public static <T> CompletableFuture<T> toCf(ListenableFuture<T> lf) {
        CompletableFuture<T> cf = new CompletableFuture<>();
        lf.addCallback(s -> cf.complete(s), e -> cf.completeExceptionally(e));
        return cf;
    }

    public static class AcceptCompletion<S> extends Completion<S, Void> {
        public Consumer<S> con;

        public AcceptCompletion(Consumer<S> con) {
            this.con = con;
        }

        @Override
        protected void run(S value) {
            con.accept(value);
        }
    }

    public static class ErrorCompletion<T> extends Completion<T, T> {
        public Consumer<Throwable> econ;

        public ErrorCompletion(Consumer<Throwable> econ) {
            this.econ = econ;
        }

        @Override
        protected void run(T value) {
            if (next != null) next.run(value);
        }

        @Override
        void error(Throwable e) {
            econ.accept(e);
        }
    }

    public static class ApplyCompletion<S, T> extends Completion<S, T> {
        public Function<S, ListenableFuture<T>> fn;

        public ApplyCompletion(Function<S, ListenableFuture<T>> fn) {
            this.fn = fn;
        }

        @Override
        protected void run(S value) {
            ListenableFuture<T> lf = fn.apply(value);
            lf.addCallback(s -> complete(s), e -> error(e));

        }
    }

    public static class Completion<S, T> {
        Completion next;

        public Completion() {
        }

        public void andAccept(Consumer<T> con) {
            Completion<T, Void> c = new AcceptCompletion<>(con);
            this.next = c;
        }

        public Completion<T, T> andError(Consumer<Throwable> econ) {
            Completion<T, T> c = new ErrorCompletion<>(econ);
            this.next = c;
            return c;
        }

        public <V> Completion<T, V> andApply(Function<T, ListenableFuture<V>> fn) {
            Completion<T, V> c = new ApplyCompletion<>(fn);
            this.next = c;
            return c;
        }

        public static <S, T> Completion<S, T> from(ListenableFuture<T> lf) {
            Completion<S, T> c = new Completion<>();
            lf.addCallback(s -> {
                c.complete(s);
            }, e -> {
                c.error(e);
            });
            return c;
        }

        void error(Throwable e) {
            if (next != null) next.error(e);
        }

        void complete(T s) {
            if (next != null) next.run(s);
        }

        void run(S value) {
        }


    }
}
