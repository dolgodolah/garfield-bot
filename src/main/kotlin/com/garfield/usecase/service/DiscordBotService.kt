package com.garfield.usecase.service

import com.garfield.domain.CallUpLol
import com.garfield.domain.LolStats
import com.garfield.usecase.port.`in`.BotUseCase
import com.garfield.usecase.port.`in`.CallUpLolCommand
import com.garfield.usecase.port.`in`.LolStatsQuery
import com.garfield.usecase.port.`in`.SayHelloCommand
import com.garfield.usecase.port.out.LolStatsUseCase
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DiscordBotService(
    private val lolStatsUseCase: LolStatsUseCase
) : BotUseCase {

    private val log = LoggerFactory.getLogger(DiscordBotService::class.java)

    override fun sayHello(sayHelloCommand: SayHelloCommand): String {
        log.info("Discord slash command /hello called with nickname={}", sayHelloCommand.nickname)
        val nickname = sayHelloCommand.nickname.trim()
        return "Hello${if (nickname.isNotBlank()) ", $nickname" else ""}!"
    }

    override fun callUpLol(callUpLolCommand: CallUpLolCommand): String {
        log.info("Discord slash command /lol called with hh:mm={}", callUpLolCommand.hhmm)
        val callUpLol = CallUpLol(
            nickname = callUpLolCommand.nickname,
            startTime = callUpLolCommand.hhmm
        )
        return callUpLol.toDiscordMessage()
    }

    override fun getLolStats(lolStatsQuery: LolStatsQuery): String {
        log.info("Discord slash command /stats called with summonerName={}, tagLine={}", lolStatsQuery.summonerName, lolStatsQuery.tagLine)
        val lolStats: LolStats = lolStatsUseCase.loadLolStats(lolStatsQuery.summonerName, lolStatsQuery.tagLine)
            ?: return "${lolStatsQuery.summonerName}#${lolStatsQuery.tagLine} 소환사를 찾을 수 없습니다."

        return lolStats.toDiscordMessage()
    }
}
