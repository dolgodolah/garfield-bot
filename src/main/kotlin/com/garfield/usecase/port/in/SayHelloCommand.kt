package com.garfield.usecase.port.`in`

import com.slack.api.bolt.context.Context
import com.slack.api.bolt.request.builtin.SlashCommandRequest

class SayHelloCommand(val nickname: String)