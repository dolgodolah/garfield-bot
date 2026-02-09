package com.garfield.domain

class CallUpLol(
    val nickname: String,
    val startTime: String?
) {

    fun toSlackMessage(): String {
        val channelMention = "<<!channel>> "
        val nicknameLine = "<@${nickname}>님이 오늘 롤 하실 분을 모십니다. 참여하실 분은 :+1: 눌러주세요."
        val startTimeLine = if (startTime.isNullOrBlank()) "" else " \n ${startTime}에 시작합니다."
        return "${channelMention}${nicknameLine}${startTimeLine}"
    }

    fun toDiscordMessage(): String {
        val everyoneMention = "@everyone "
        val nicknameLine = "<<@${nickname}>>님이 오늘 롤 하실 분을 모십니다. 참여하실 분은 :+1: 눌러주세요."
        val startTimeLine = if (startTime.isNullOrBlank()) "" else " \n ${startTime}에 시작합니다."
        return "${everyoneMention}${nicknameLine}${startTimeLine}"
    }
}