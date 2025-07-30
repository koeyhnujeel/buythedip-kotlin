package com.zunza.buythedip_kotlin.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@Profile("!test")
@EnableScheduling
class SchedulerConfig {
}
