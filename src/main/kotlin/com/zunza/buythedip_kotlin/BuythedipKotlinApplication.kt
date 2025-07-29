package com.zunza.buythedip_kotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
class BuythedipKotlinApplication

fun main(args: Array<String>) {
	runApplication<BuythedipKotlinApplication>(*args)
}
