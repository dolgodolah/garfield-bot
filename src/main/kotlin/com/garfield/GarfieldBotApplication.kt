package com.garfield

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GarfieldBotApplication

fun main(args: Array<String>) {
	runApplication<GarfieldBotApplication>(*args)
}
