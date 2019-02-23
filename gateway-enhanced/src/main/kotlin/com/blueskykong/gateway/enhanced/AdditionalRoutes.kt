package com.blueskykong.gateway.enhanced

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AdditionalRoutes {

	@Bean
	open fun additionalRouteLocator(builder: RouteLocatorBuilder): RouteLocator = builder.routes {
		route(id = "kotlin-route") {
			path("/blog/archives")
			filters {
				stripPrefix(1)
				addResponseHeader("X-Content-Source", "blog")
			}
			uri("http://blueskykong.com")
		}
	}


}