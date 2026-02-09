package com.garfield.usecase.port.`in`

interface BotUseCase {
    fun sayHello(sayHelloCommand: SayHelloCommand): String
    fun callUpLol(callUpLolCommand: CallUpLolCommand): String
}
