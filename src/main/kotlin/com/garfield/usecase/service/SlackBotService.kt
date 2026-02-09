package com.garfield.usecase.service

import com.garfield.domain.CallUpLol
import com.garfield.usecase.port.`in`.BotUseCase
import com.garfield.usecase.port.`in`.CallUpLolCommand
import com.garfield.usecase.port.`in`.SayHelloCommand
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SlackBotService : BotUseCase {

    private val log = LoggerFactory.getLogger(SlackBotService::class.java)

    override fun sayHello(sayHelloCommand: SayHelloCommand): String {
        log.info("Slack slash command /hello called with nickname={}", sayHelloCommand.nickname)
        val nickname = sayHelloCommand.nickname.trim()
        return "Hello${if (nickname.isNotBlank()) ", $nickname" else ""}!"
    }

    override fun callUpLol(callUpLolCommand: CallUpLolCommand): String {
        log.info("Slack slash command /lol called with hh:mm={}", callUpLolCommand.hhmm)
        val callUpLol = CallUpLol(
            nickname = callUpLolCommand.nickname,
            startTime = callUpLolCommand.hhmm
        )
        return callUpLol.toSlackMessage()
    }
}
