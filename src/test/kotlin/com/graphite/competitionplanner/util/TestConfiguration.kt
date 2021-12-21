package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.security.PathAccessInterceptor
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@TestConfiguration
class InterceptorRegistry(val pathAccessInterceptor: PathAccessInterceptor) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(pathAccessInterceptor)
    }
}