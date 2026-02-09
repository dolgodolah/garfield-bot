package com.garfield.usecase.port.`in`

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload

class CallUpLolCommand(
    val nickname: String,
    val hhmm: String?
) {
    companion object {
        fun normalizeHhmm(rawText: String?): String? {
            return rawText
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?.let { input ->
                    val compact = input.replace(":", "")
                    when {
                        compact.matches(Regex("^\\d{4}$")) ->
                            "${compact.substring(0, 2)}:${compact.substring(2, 4)}"
                        input.matches(Regex("^\\d{1,2}:\\d{2}$")) -> input
                        else -> input
                    }
                }
        }

        fun of(payload: SlashCommandPayload): CallUpLolCommand {
            val normalizedHhmm = normalizeHhmm(payload.text)
            return CallUpLolCommand(
                nickname = payload.userName,
                hhmm = normalizedHhmm
            )
        }
    }
}
