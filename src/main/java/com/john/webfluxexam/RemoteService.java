package com.john.webfluxexam;

import com.john.webfluxexam.controller.ReactiveController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class RemoteService {

    @Bean
    @Profile("tomcat")
    TomcatReactiveWebServerFactory tomcatReactiveWebServerFactory() {
        return new TomcatReactiveWebServerFactory();
    }

    @RestController
    public static class MyController {
        @GetMapping("/service")
        public String service(String req) throws InterruptedException {
            Thread.sleep(1000);
//            throw new RuntimeException();
            return req + "/service1";
        }

        @GetMapping("/service2")
        public String service2(String req) throws InterruptedException {
            Thread.sleep(1000);
            return req + "/service2";
        }
    }

    public static void main(String[] args) {
        System.setProperty("server.port", "8081");
        System.setProperty("server.tomcat.max-threads", "1000");
        SpringApplication.run(RemoteService.class, args);
    }


}
