package com.blueskykong.gateway.enhanced.config;

import com.blueskykong.gateway.enhanced.handler.FallbackHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author keets
 * @date 2019-02-22.
 */
@Configuration
@EnableWebFlux
public class WebfluxRouteConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(FallbackHandler fallbackHandler){
        return route(GET("/fallbackcontroller"), fallbackHandler::all)
                .andRoute(POST("/fallbackcontroller"), fallbackHandler::all);
    }


}
