package com.graphite.competitionplanner

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import java.nio.charset.StandardCharsets


@Configuration
class Configuration {

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun springApplicationContext(): SpringApplicationContext? {
        return SpringApplicationContext()
    }

    @Bean
    fun mappingJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter? {
        val jsonConverter = MappingJackson2HttpMessageConverter()
        jsonConverter.defaultCharset = StandardCharsets.UTF_8
        return jsonConverter
    }
}