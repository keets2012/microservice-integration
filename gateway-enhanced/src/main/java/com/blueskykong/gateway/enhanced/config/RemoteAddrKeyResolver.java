package com.blueskykong.gateway.enhanced.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author keets
 * @data 2018/5/19.
 */
public class RemoteAddrKeyResolver implements KeyResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteAddrKeyResolver.class);

    public static final String BEAN_NAME = "remoteAddrKeyResolver";

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        LOGGER.debug("token limit for ip: {} ", exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
        return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

}
