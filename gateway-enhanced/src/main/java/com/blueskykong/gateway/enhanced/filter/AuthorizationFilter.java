package com.blueskykong.gateway.enhanced.filter;

import com.blueskykong.gateway.enhanced.http.HeaderEnhanceFilter;
import com.blueskykong.gateway.enhanced.properties.PermitAllUrlProperties;
import com.blueskykong.gateway.enhanced.security.CustomRemoteTokenServices;
import com.blueskykong.gateway.enhanced.security.OAuth2AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * @author keets
 * @data 2018/9/18.
 */
public class AuthorizationFilter implements GlobalFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilter.class);

    private final CustomRemoteTokenServices customRemoteTokenServices;

    private final HeaderEnhanceFilter headerEnhanceFilter;

    private PermitAllUrlProperties permitAllUrlProperties;

    public AuthorizationFilter(CustomRemoteTokenServices customRemoteTokenServices, HeaderEnhanceFilter headerEnhanceFilter,
                               PermitAllUrlProperties permitAllUrlProperties) {
        this.customRemoteTokenServices = customRemoteTokenServices;
        this.headerEnhanceFilter = headerEnhanceFilter;
        this.permitAllUrlProperties = permitAllUrlProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (predicate(exchange)) {
            request = headerEnhanceFilter.doFilter(request);
            String accessToken = extractHeaderToken(request);

            customRemoteTokenServices.loadAuthentication(accessToken);
            LOGGER.info("success auth token and permission!");
        }

        return chain.filter(exchange);
    }

    public Boolean predicate(ServerWebExchange serverWebExchange) {
        URI uri = serverWebExchange.getRequest().getURI();
        String requestUri = uri.getPath();
        String authorization = serverWebExchange.getRequest().getHeaders().getFirst("Authorization");
        if (isPermitUrl(requestUri) && (StringUtils.isBlank(authorization) || (StringUtils.isNotBlank(authorization) &&
                StringUtils.countMatches(authorization, ".") != 2))) {
            return false;
        }
        if (isLogoutUrl(requestUri)) {
            return false;
        }
        return true;
    }

    private boolean isLogoutUrl(String url) {
        return url.contains("/login/logout");
    }

    private boolean isPermitUrl(String url) {
        return permitAllUrlProperties.isPermitAllUrl(url) || url.contains("/login/oauth");
    }

    protected String extractHeaderToken(ServerHttpRequest request) {
        List<String> headers = request.getHeaders().get("Authorization");
        if (Objects.nonNull(headers) && headers.size() > 0) { // typically there is only one (most servers enforce that)
            String value = headers.get(0);
            if ((value.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase()))) {
                String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
                // Add this here for the auth details later. Would be better to change the signature of this method.
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
        }

        return null;
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return -200;
    }
}
