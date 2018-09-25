package com.blueskykong.gateway.enhanced.config;

import com.blueskykong.gateway.enhanced.filter.RateLimitByIpGatewayFilter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;


/**
 * Created by keets on 2018/2/11.
 */
@EnableAutoConfiguration
@Configuration
public class RouteConfiguration {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        //@formatter:off
        return builder.routes()
                .route(r -> r.path("/**/inner/**")
                        .uri("http://localhost:9099/error").order(-102)
                )
                .route("read_body_pred", r -> r.path("/test10/test2").and().readBody(String.class,
                        s -> {
                            JsonObject jsonObject = (JsonObject) new Gson().fromJson(s, JsonObject.class);
                            jsonObject.entrySet().size();
                            String val = jsonObject.get("key").getAsString();

                            return val.equalsIgnoreCase("hello");
//                            return s.get("key").getAsString().equals("hello");
                        }).filters(f -> f.addResponseHeader("X-TestHeader", "read_body_pred"))
                                .uri("http://httpbin.org")
                ).route("modify_request_body", r -> r.path("/modify/test2")
                        .filters(f -> f.addRequestHeader("X-TestHeader", "rewrite_request_upper")
                                .modifyRequestBody(String.class, String.class,
                                        (exchange, s) -> Mono.just(s.toUpperCase())).modifyResponseBody(String.class, String.class,
                                        (exchange, s) -> Mono.just("12345"))
                        ).uri("http://localhost:8005/body")
                )
                .route(r -> r.path("/test5/**").and().header("X-Next-Url", ".+")
                        .filters(f -> f.requestHeaderToRequestUri("X-Next-Url"))
                        .uri("http://baidu.com"))
                .route(r -> r.path("/test6/**").and().query("url")
                        .filters(f -> f.changeRequestUri(e -> Optional.of(URI.create(
                                e.getRequest().getQueryParams().getFirst("url")))))
                        .uri("http://example.com"))
                .route(r -> r.path("/customer/**")
                        .filters(f -> f.stripPrefix(2)
                                .filter(new RateLimitByIpGatewayFilter(10, 1, Duration.ofSeconds(1))))
                        .uri("http://baidu.com")
                        .order(0)
                        .id("throttle_customer_service"))
                .build();
        //@formatter:on
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.host("**.changeuri.org").and().header("X-Next-Url")
                        .filters(f -> f.requestHeaderToRequestUri("X-Next-Url"))
                        .uri("http://blueskykong.com"))
                .route(r -> r.host("**.changeuri.org").and().query("url")
                        .filters(f -> f.changeRequestUri(e -> Optional.of(URI.create(
                                e.getRequest().getQueryParams().getFirst("url")))))
                        .uri("http://blueskykong.com"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> testFunRouterFunction() {
        RouterFunction<ServerResponse> route = RouterFunctions.route(
                RequestPredicates.path("/testfun"),
                request -> ServerResponse.ok().body(BodyInserters.fromObject("hello")));
        return route;
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("user"));
    }

    @Bean(name = RemoteAddrKeyResolver.BEAN_NAME)
    public RemoteAddrKeyResolver remoteAddrKeyResolver() {
        return new RemoteAddrKeyResolver();
    }

    @Bean
    public RouteLocator retryRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("retry_java", r -> r.path("/test/**")
                        .filters(f -> f.stripPrefix(1)
                                .retry(config -> config.setRetries(2).setStatuses(HttpStatus.INTERNAL_SERVER_ERROR)))
                        .uri("lb://user"))
                .build();
    }

}
