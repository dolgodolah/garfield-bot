package com.garfield.usecase.port.`in`

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload

data class LolStatsQuery(
    val summonerName: String,
    val tagLine: String = "KR1"
) {
    companion object {
        fun of(payload: SlashCommandPayload): LolStatsQuery {
            return of(payload.text)
        }

        fun of(text: String): LolStatsQuery {
            val normalized = text.trim()
            val split = normalized.split("#", limit = 2)
            val summonerName = split[0].trim()

            return LolStatsQuery(
                summonerName = summonerName,
                tagLine = split.getOrNull(1)?.trim().takeUnless { it.isNullOrBlank() } ?: "KR1"
            )
        }
    }
}
