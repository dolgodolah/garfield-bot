package com.garfield.adapter.out

import com.garfield.adapter.out.client.RiotGamesClient
import com.garfield.domain.FlexRank
import com.garfield.domain.LolStats
import com.garfield.domain.QueueType
import com.garfield.domain.SoloRank
import com.garfield.domain.Tier
import com.garfield.usecase.port.out.LolStatsUseCase
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap

@Component
class LolStatsAdapter(
    private val riotGamesClient: RiotGamesClient
) : LolStatsUseCase {

    private val riotAccountCache =
        ConcurrentHashMap<String, Optional<RiotGamesClient.RiotAccountResponse>>()

    override fun loadLolStats(summonerName: String, tagLine: String): LolStats? {
        val riotAccountCacheKey = buildRiotAccountCacheKey(summonerName, tagLine)
        val riotAccount = riotAccountCache.computeIfAbsent(riotAccountCacheKey) {
            Optional.ofNullable(riotGamesClient.getRiotAccountByRiotId(summonerName, tagLine))
        }.orElse(null)
            ?: return null

        val leagueEntries = riotGamesClient.getLeagueEntriesByPuuid(riotAccount.puuid)
        val soloRank = leagueEntries.find {
            it.queueType == QueueType.RANKED_SOLO_5x5.name
        }
        val flexRank = leagueEntries.find {
            it.queueType == QueueType.RANKED_FLEX_SR.name
        }

        return LolStats(
            summonerName = riotAccount.gameName,
            tagLine = riotAccount.tagLine,
            soloRank = soloRank?.let {
                SoloRank(
                    tier = it.tier?.let { tier -> Tier.valueOf(tier) },
                    rank = it.rank,
                    leaguePoints = it.leaguePoints,
                    wins = it.wins,
                    losses = it.losses
                )
            },
            flexRank = flexRank?.let {
                FlexRank(
                    tier = it.tier?.let { tier -> Tier.valueOf(tier) },
                    rank = it.rank,
                    leaguePoints = it.leaguePoints,
                    wins = it.wins,
                    losses = it.losses
                )
            }
        )
    }

    private fun buildRiotAccountCacheKey(summonerName: String, tagLine: String): String {
        val normalizedSummonerName = summonerName.trim().removeAllWhitespace().lowercase()
        val normalizedTagLine = tagLine.trim().removeAllWhitespace().lowercase()
        return "$normalizedSummonerName#$normalizedTagLine"
    }
}

private fun String.removeAllWhitespace(): String = replace("\\s+".toRegex(), "")
