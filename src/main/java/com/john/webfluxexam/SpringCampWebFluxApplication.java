package com.john.webfluxexam;

import com.john.webfluxexam.handler.HelloHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@SpringBootApplication
@Slf4j
@RestController
public class SpringCampWebFluxApplication {

    // step 1
//    @GetMapping("/hello/{name}")
//    String hello(@PathVariable String name) {
//        return "Hello" + name;
//    }

    // step 2
    RouterFunction router = req -> RequestPredicates.path("/hello/{name}").test(req) ?
        Mono.just(helloHandler) : Mono.empty();
    static HandlerFunction helloHandler = req -> {
        String name = req.pathVariable("name");
        Mono<String> result = Mono.just("Hello" + name);
        Mono<ServerResponse> res = ServerResponse.ok().body(result, String.class);
        return res;
    };

    // step 3
    RouterFunction router2 = RouterFunctions.route(RequestPredicates.path("/hello2/{name}"),
            req -> ServerResponse.ok().body(fromObject("Hello2" + req.pathVariable("name"))));


    // RouterFunction 의 등록 : RouterFunction 타입의 @Bean 으로 만든다.
    @Bean
    RouterFunction helloPathVarRouter() {
        return RouterFunctions.route(RequestPredicates.path("/hello3/{name}"),
                req -> ServerResponse.ok().body(fromObject("Hello3 " + req.pathVariable("name"))));
    }

    // 핸들러 내부 로직이 복잡하다면 분리한다
    // - 핸들러 코드만 람다 식을 따로 선언
    // - 메소드를 정의하고 메소드 참조로 가져온다
    @Bean
    RouterFunction helloRouter(@Autowired HelloHandler helloHandler) {
        return RouterFunctions.route(RequestPredicates.path("/hello/{name}"), helloHandler::hello);
    }

    // RouterFunction 의 조합
    // - 핸들러 하나의 @Bean 하나씩 만들어야 하나?
    // - RouterFunction 의 and(), andRoute() 등으로 하나의 @Bean 에 n 개의 RouterFunction 을 선언할 수 있다.

    // RouterFunction 의 매핑 조건의 중복
    // - 타입 레벨 - 메소드 레벨의 @RequestMapping 처럼 공통의 조건을 정의하는 것 가능
    // - RouterFunction.nest()

    // WebFlux 함수형 스타일의 장점
    // - 모든 웹 요청 처리 작업을 명시적인 코드로 작성
    // -- 메소드 시그니처 관례와 타입 체크가 불가능한 애노테이션에 의존하는 @MVC 스타일보다 명확
    // -- 정확한 타입 체크 가능
    // - 함수 조합을 통한 편리한 구성, 추상화에 유리
    // - 테스트 작성의 편리함
    // -- 핸들러 로직은 물론이고 요청 매핑과 리턴 값 처리까지 단위테스트로 작성 가능

    // WebFlux 함수형 스타일의 단점
    // - 함수형 스타일의 코드 작성이 편하지 않으면 코드 작성과 이해 모두 어려움
    // - 익숙한 방식으로도 가능한데 뭐하러

    // @MVC WebFlux
    // - 애노테이션과 메소드 형식의 관례를 이용하는 @MVC 방식과 유사
    // - 비동기 + 논블록킹 리액티브 스타일로 작성

    // ServerRequest, ServerResponse
    // - WebFlux 의 기본 요청, 응답 인터페이스 사용
    // - 함수형 WebFlux 의 HandlerFunction 을 메소드로 만들었을 때와 유사
    // - 매핑만 애노테이션 방식을 이용

    @RequestMapping("/hello4/{name}")
    Mono<ServerResponse> hello4(ServerRequest req) {
        return ServerResponse.ok().body(fromObject(req.pathVariable("name")));
    }

    // @MVC 요청 바인딩과 Mono / Flux 리턴 값
    // - 가장 대표적인 @MVC WebFlux 작성 방식
    // - 파라미터 바인딩은 @MVC 방식 그대로
    // - 핸들러 로직 코드의 결과를 Mono / Flux 타입으로 리턴

    // @MVC 와 동일한 바인딩
    // - 경로 변수
    // - 커맨드 오브젝트
    // - 폼 오브젝트, 모델 애트리뷰트

    // @RequestBody 바인딩 (JSON, XML)
    // - T
    // - Mono<T>
    // - Flux<T>

    // @ResponseBody 리턴 값 타입
    // - T
    // - Mono<T>
    // - Flux<T>
    // - Flux<ServerSideEvent>
    // - void
    // - Mono<Void>



   public static void main(String[] args) {
        SpringApplication.run(SpringCampWebFluxApplication.class, args);
    }
}
