package com.garfield.adapter.`in`

import com.garfield.usecase.port.`in`.BotUseCase
import com.garfield.usecase.port.`in`.CallUpLolCommand
import com.garfield.usecase.port.`in`.SayHelloCommand
import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.socket_mode.SocketModeApp
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.SmartLifecycle
import org.springframework.stereotype.Component
import kotlin.concurrent.thread

@Configuration
class SlackBot(
    @Value($$"${slack.bot-token}") private val botToken: String,
    @Value($$"${slack.app-token}") private val appToken: String,
    private val slackBotHandler: SlackBotHandler,
) {

    private val logger = LoggerFactory.getLogger(SlackBot::class.java)

    @Bean
    fun run(): App {
        val config = AppConfig.builder()
            .singleTeamBotToken(botToken)
            .build()

        return App(config).apply {
            slackBotHandler.route(this)
        }
    }

    @Bean
    fun socketModeApp(app: App): SocketModeApp {
        return SocketModeApp(appToken, app)
    }

    @Bean
    fun socketModeLifecycle(socketModeApp: SocketModeApp): SmartLifecycle {
        return object : SmartLifecycle {
            @Volatile
            private var running = false

            override fun start() {
                if (running) return
                running = true
                thread(name = "slack-socket-mode", isDaemon = true) {
                    logger.info("Starting Slack socket mode app")
                    socketModeApp.start()
                }
            }

            override fun stop() {
                if (!running) return
                logger.info("Stopping Slack socket mode app")
                socketModeApp.stop()
                running = false
            }

            override fun isRunning(): Boolean = running
            override fun isAutoStartup(): Boolean = true
            override fun getPhase(): Int = Integer.MAX_VALUE
        }
    }
}

@Component
class SlackBotHandler(
    @Qualifier("slackBotService")
    private val botUseCase: BotUseCase
) {

    fun route(app: App) {
        app.command("/hello") { req, ctx ->
            val nickname = req.payload.text?.trim().orEmpty()
            val message = botUseCase.sayHello(SayHelloCommand(nickname))
            req.postMessage(message)
            ctx.ack()
        }.command("/lol") { req, ctx ->
            val message = botUseCase.callUpLol(CallUpLolCommand.of(req.payload))
            req.postMessage(message)
            ctx.ack()
        }
    }
}

fun SlashCommandRequest.postMessage(message: String) {
    this.context.client().chatPostMessage {
        it.channel(this.payload.channelId)
        it.text(message)
    }
}
