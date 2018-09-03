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

import java.util.function.Consumer;


@RestController
public class MyTestController {

    @Autowired
    MyService myService;

    AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

    // callback을 받아 비동기로 처리
    @GetMapping("/rest")
    public DeferredResult<String> rest(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();

        // https://www.youtube.com/watch?v=Tb43EyWTSlQ / 26분 50초 확인
        Completion
                .from(rt.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" + idx))
                .andApply(s ->rt.getForEntity("http://localhost:8081/service2?req={req}", String.class, s.getBody()))
                .andAccept(s -> dr.setResult(s.getBody()));
        //        ListenableFuture<ResponseEntity<String>> f1 = rt.getForEntity("http://localhost:8081/service?req={req}",
        //                String.class, "hello" + idx);
        //        f1.addCallback(s -> {
        //            ListenableFuture<ResponseEntity<String>> f2 = rt.getForEntity("http://localhost:8081/service2?req={req}",
        //                    String.class, s.getBody());
        //            f2.addCallback(s2 -> {
        //                        ListenableFuture<String> f3 = myService.work(s2.getBody());
        //                        f3.addCallback(s3 -> {
        //                            dr.setResult(s3);
        //                        }, e -> {
        //                            dr.setErrorResult(e.getMessage());
        //                        });
        //
        //                    }, e -> {
        //                        dr.setErrorResult(e.getMessage());
        //                    }
        //            );
        //        }, e -> {
        //            dr.setErrorResult(e.getMessage());
        //        });

        return dr;
    }

    public static class Completion {
        Completion next;
        Consumer<ResponseEntity<String>> con;

        public Completion() {

        }

        public Completion(Consumer<ResponseEntity<String>> con) {
            this.con = con;
        }


        public void andAccept(Consumer<ResponseEntity<String>> con) {
            Completion c = new Completion(con);
            this.next = c;
        }

        public static Completion from(ListenableFuture<ResponseEntity<String>> lf) {
            Completion c = new Completion();
            lf.addCallback(s -> {
                c.complete(s);
            }, e -> {
                c.error(e);
            });
            return c;
        }

        private void complete(ResponseEntity<String> s) {
            if (next != null)
                next.run(s);
        }

        private void run(ResponseEntity<String> value) {
            if (con != null)
                con.accept(value);
        }

        private void error(Throwable e) {
        }
    }
}
