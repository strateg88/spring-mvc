package com.muratov.springmvc.configuration

import com.muratov.springmvc.filter.LoggerFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class Common {
    @Bean
    fun logger(): Logger {
        return LoggerFactory.getLogger(LoggerFilter::class.java)
    }
}