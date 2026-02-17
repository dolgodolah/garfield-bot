package com.garfield.adapter.out

import com.garfield.adapter.out.client.RiotGamesApi
import com.garfield.domain.FlexRank
import com.garfield.domain.LolStats
import com.garfield.domain.QueueType
import com.garfield.domain.SoloRank
import com.garfield.domain.Tier
import com.garfield.usecase.port.out.LolStatsUseCase
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class LolStatsAdapter(
    @Qualifier("cachingRiotGamesApiClient")
    private val riotGamesApi: RiotGamesApi
) : LolStatsUseCase {

    override fun loadLolStats(summonerName: String, tagLine: String): LolStats? {
        val riotAccount = riotGamesApi.getRiotAccountByRiotId(summonerName, tagLine)
            ?: return null

        val leagueEntries = riotGamesApi.getLeagueEntriesByPuuid(riotAccount.puuid)
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
}
