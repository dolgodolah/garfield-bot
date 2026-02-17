package com.garfield.adapter.out.client

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Component
class CachingRiotGamesApiClient(
    @Qualifier("riotGamesApiClient") private val delegate: RiotGamesApi
) : RiotGamesApi {

    private val riotAccountCache = ConcurrentHashMap<String, CacheEntry>()

    override fun getRiotAccountByRiotId(gameName: String, tagLine: String): RiotAccountResponse? {
        val cacheKey = buildRiotAccountCacheKey(gameName, tagLine)
        val now = System.currentTimeMillis()

        val cacheEntry = riotAccountCache.compute(cacheKey) { _, existingEntry ->
            if (existingEntry != null && existingEntry.expiresAt > now) {
                return@compute existingEntry
            }

            val riotAccount = delegate.getRiotAccountByRiotId(gameName, tagLine)
            CacheEntry(riotAccount = riotAccount, expiresAt = now + 1.hours.inWholeMilliseconds)
        }

        return cacheEntry?.riotAccount
    }

    override fun getLeagueEntriesByPuuid(puuid: String): List<RiotLeagueEntryResponse> {
        return delegate.getLeagueEntriesByPuuid(puuid)
    }

    private fun buildRiotAccountCacheKey(summonerName: String, tagLine: String): String {
        val normalizedSummonerName = summonerName.trim().removeAllWhitespace().lowercase()
        val normalizedTagLine = tagLine.trim().removeAllWhitespace().lowercase()
        return "$normalizedSummonerName#$normalizedTagLine"
    }

    private data class CacheEntry(
        val riotAccount: RiotAccountResponse?,
        val expiresAt: Long,
    )
}

private fun String.removeAllWhitespace(): String = replace("\\s+".toRegex(), "")
