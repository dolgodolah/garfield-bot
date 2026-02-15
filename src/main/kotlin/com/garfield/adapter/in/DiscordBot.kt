package com.garfield.adapter.`in`

import com.garfield.usecase.port.`in`.BotUseCase
import com.garfield.usecase.port.`in`.CallUpLolCommand
import com.garfield.usecase.port.`in`.LolStatsQuery
import com.garfield.usecase.port.`in`.SayHelloCommand
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class DiscordBot(
    @Value($$"${discord.bot-token}") private val botToken: String,
    private val discordBotHandler: DiscordBotHandler
) {

    private val logger = LoggerFactory.getLogger(DiscordBot::class.java)

    @Bean
    fun jda(): JDA {
        logger.info("Starting Discord bot")
        val jda = JDABuilder.createDefault(botToken)
            .addEventListeners(discordBotHandler)
            .build()

        jda.updateCommands()
            .addCommands(
                Commands.slash("hello", "인사합니다")
                    .addOption(OptionType.STRING, "nickname", "닉네임", false),
                Commands.slash("lol", "훈련 소집")
                    .addOption(OptionType.STRING, "time", "시작 시간 (예: 1830 또는 18:30)", false),
                Commands.slash("stats", "소환사 전적 조회")
                    .addOption(OptionType.STRING, "summoner_name", "소환사명 (예: Hide on bush, Hide on bush#KR1)", true)
                    .addOption(OptionType.STRING, "tag", "태그 (예: KR1)", false),
            )
            .queue()

        return jda
    }
}

@Component
class DiscordBotHandler(
    @Qualifier("discordBotService")
    private val botUseCase: BotUseCase
) : ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "hello" -> {
                val nickname = event.getOption("nickname")?.asString.orEmpty()
                val message = botUseCase.sayHello(SayHelloCommand(nickname))
                event.reply(message).queue()
            }
            "lol" -> {
                val time = event.getOption("time")?.asString
                val message = botUseCase.callUpLol(
                    CallUpLolCommand(
                        nickname = event.user.id,
                        hhmm = CallUpLolCommand.normalizeHhmm(time)
                    )
                )
                event.reply(message).queue()
            }
            "stats" -> {
                val summonerName = event.getOption("summoner_name")?.asString.orEmpty()
                val tagLine = event.getOption("tag")?.asString.orEmpty()
                val command = if (tagLine.isEmpty()) {
                    summonerName
                } else {
                    "$summonerName#$tagLine"
                }
                val message = botUseCase.getLolStats(LolStatsQuery.of(command))
                event.reply(message).queue()
            }
        }
    }
}
