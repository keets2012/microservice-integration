package com.blueskykong.gateway.enhanced.config;

import com.blueskykong.gateway.enhanced.exception.ErrorCodes;
import com.blueskykong.gateway.enhanced.exception.ServerException;
import com.blueskykong.gateway.enhanced.filter.AuthorizationFilter;
import com.blueskykong.gateway.enhanced.filter.ThrottleGatewayFilterFactory;
import com.blueskykong.gateway.enhanced.http.HeaderEnhanceFilter;
import com.blueskykong.gateway.enhanced.properties.GatewayLimitProperties;
import com.blueskykong.gateway.enhanced.properties.PermitAllUrlProperties;
import com.blueskykong.gateway.enhanced.properties.ResourceServerProperties;
import com.blueskykong.gateway.enhanced.security.CustomRemoteTokenServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author keets
 * @date 2017/9/29
 */
@Configuration
@EnableConfigurationProperties
@RibbonClient(name = "auth")
public class ServiceConfig {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private ResourceServerProperties resource;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Order(100)
    public CustomRemoteTokenServices customRemoteTokenServices(RestTemplate restTemplate) {
        CustomRemoteTokenServices resourceServerTokenServices = new CustomRemoteTokenServices(restTemplate);
        resourceServerTokenServices.setCheckTokenEndpointUrl(resource.getResource().getTokenInfoUri());
        resourceServerTokenServices.setClientId(resource.getClient().getClientId());
        resourceServerTokenServices.setClientSecret(resource.getClient().getClientSecret());
        resourceServerTokenServices.setLoadBalancerClient(loadBalancerClient);
        return resourceServerTokenServices;
    }

    @Bean
    public AuthorizationFilter authorizationFilter(CustomRemoteTokenServices customRemoteTokenServices,
                                                   HeaderEnhanceFilter headerEnhanceFilter,
                                                   PermitAllUrlProperties permitAllUrlProperties) {
        return new AuthorizationFilter(customRemoteTokenServices, headerEnhanceFilter, permitAllUrlProperties);
    }

    @Bean
    public ThrottleGatewayFilterFactory throttleGatewayFilterFactory(GatewayLimitProperties gatewayLimitProperties) {
        GatewayLimitProperties.Throttle throttle = gatewayLimitProperties.getThrottle();
        if (Objects.isNull(throttle)) {
            throw new ServerException(ErrorCodes.PROPERTY_NOT_INITIAL);
        }
        return new ThrottleGatewayFilterFactory(throttle.getCapacity(), throttle.getRefillTokens(),
                throttle.getRefillPeriod(), TimeUnit.MILLISECONDS);
    }

    @Bean
    public HeaderEnhanceFilter headerEnhanceFilter() {
        return new HeaderEnhanceFilter();
    }

    @Bean(name = "customRateLimiter")
    @Primary
    public RedisRateLimiter myRateLimiter(GatewayLimitProperties gatewayLimitProperties) {
        GatewayLimitProperties.RedisRate redisRate = gatewayLimitProperties.getRedisRate();
        if (Objects.isNull(redisRate)) {
            throw new ServerException(ErrorCodes.PROPERTY_NOT_INITIAL);
        }
        return new RedisRateLimiter(redisRate.getReplenishRate(), redisRate.getBurstCapacity());
    }

}
