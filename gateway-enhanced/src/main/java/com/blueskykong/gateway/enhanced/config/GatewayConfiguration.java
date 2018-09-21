package com.blueskykong.gateway.enhanced.config;

import com.blueskykong.gateway.enhanced.properties.GatewayLimitProperties;
import com.blueskykong.gateway.enhanced.properties.PermitAllUrlProperties;
import com.blueskykong.gateway.enhanced.properties.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author keets
 * @data 2018/8/20.
 */
@Configuration
public class GatewayConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "auth")
    public PermitAllUrlProperties getPermitAllUrlProperties() {
        return new PermitAllUrlProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "gateway.limit")
    public GatewayLimitProperties gatewayLimitProperties() {
        return new GatewayLimitProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "security.oauth2")
    public ResourceServerProperties resourceServerProperties() {
        return new ResourceServerProperties();
    }

}
