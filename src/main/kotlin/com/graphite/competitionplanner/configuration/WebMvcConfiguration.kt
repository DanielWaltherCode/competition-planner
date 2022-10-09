package com.graphite.competitionplanner.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.CookieLocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor

@Configuration
class WebMvcConfiguration : WebMvcConfigurer {

    @Bean
    fun localeResolver(): LocaleResolver {
        return CookieLocaleResolver()
    }

    @Bean
    fun localeInterceptor(): LocaleChangeInterceptor {
        val localeInterceptor = LocaleChangeInterceptor();
        localeInterceptor.paramName = "lang";
        return localeInterceptor;
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(localeInterceptor());
    }
}