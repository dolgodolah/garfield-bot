package com.garfield.domain

data class LolStats(
    val summonerName: String,
    val tagLine: String,
    val soloRank: SoloRank?,
    val flexRank: FlexRank?
) {

    fun toDiscordMessage(): String {
        val soloRankMessage = soloRank?.let { "솔로랭크: ${it.tier} ${it.rank} (${it.leaguePoints}LP, ${it.wins}승 ${it.losses}패)" } ?: "솔로랭크: 없음"
        val flexRankMessage = flexRank?.let { "자유랭크: ${it.tier} ${it.rank} (${it.leaguePoints}LP, ${it.wins}승 ${it.losses}패)" } ?: "자유랭크: 없음"

        return "소환사명: $summonerName#$tagLine\n$soloRankMessage\n$flexRankMessage"
    }

    fun toSlackMessage(): String {
        val soloRankMessage = soloRank?.let { "솔로랭크: ${it.tier} ${it.rank} (${it.leaguePoints}LP, ${it.wins}승 ${it.losses}패)" } ?: "솔로랭크: 없음"
        val flexRankMessage = flexRank?.let { "자유랭크: ${it.tier} ${it.rank} (${it.leaguePoints}LP, ${it.wins}승 ${it.losses}패)" } ?: "자유랭크: 없음"

        return "소환사명: $summonerName#$tagLine\n$soloRankMessage\n$flexRankMessage"
    }
}

data class SoloRank(
    val tier: Tier?,
    val rank: String?,
    val leaguePoints: Int?,
    val wins: Int?,
    val losses: Int?
)

data class FlexRank(
    val tier: Tier?,
    val rank: String?,
    val leaguePoints: Int?,
    val wins: Int?,
    val losses: Int?
)

enum class Tier {
    BRONZE,
    SILVER,
    GOLD,
    PLATINUM,
    EMERALD,
    DIAMOND,
    MASTER,
    GRANDMASTER,
    CHALLENGER
}

enum class QueueType {
    RANKED_SOLO_5x5,
    RANKED_FLEX_SR,
    RANKED_FLEX_TT
}