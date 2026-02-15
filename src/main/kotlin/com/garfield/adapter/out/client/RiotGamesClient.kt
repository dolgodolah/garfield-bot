package com.garfield.adapter.out.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.body

@Component
class RiotGamesClient(
    @Value("\${riot.api-key}") private val riotApiKey: String,
    @Value("\${riot.account-api.base-url:https://asia.api.riotgames.com}") private val accountApiBaseUrl: String,
    @Value("\${riot.league-api.base-url:https://kr.api.riotgames.com}") private val leagueApiBaseUrl: String,
) {

    private val log = LoggerFactory.getLogger(RiotGamesClient::class.java)

    private val accountRestClient: RestClient = RestClient.builder()
        .baseUrl(accountApiBaseUrl)
        .defaultHeader("X-Riot-Token", riotApiKey)
        .build()

    private val leagueRestClient: RestClient = RestClient.builder()
        .baseUrl(leagueApiBaseUrl)
        .defaultHeader("X-Riot-Token", riotApiKey)
        .build()

    fun getRiotAccountByRiotId(gameName: String, tagLine: String): RiotAccountResponse? {
        val trimmedGameName = gameName.trim()
        val trimmedTagLine = tagLine.trim()
        require(trimmedGameName.isNotEmpty()) { "gameName must not be blank." }
        require(trimmedTagLine.isNotEmpty()) { "tagLine must not be blank." }

        val gameNameCandidates = buildRiotIdCandidates(trimmedGameName)
        val tagLineCandidates = buildRiotIdCandidates(trimmedTagLine)

        gameNameCandidates.forEach { candidateGameName ->
            tagLineCandidates.forEach { candidateTagLine ->
                val account = getRiotAccountByRiotIdOrNull(candidateGameName, candidateTagLine)
                if (account != null) {
                    return account
                }
            }
        }

        return null
    }

    private fun getRiotAccountByRiotIdOrNull(gameName: String, tagLine: String): RiotAccountResponse? {
        try {
            return accountRestClient.get()
                .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}", gameName, tagLine)
                .retrieve()
                .body<RiotAccountResponse>()
                ?: throw IllegalStateException("Riot account response is empty.")
        } catch (e: RestClientResponseException) {
            if (e.statusCode.is4xxClientError) {
                log.info(
                    "Riot id not found or invalid request ({}#{}). status={}",
                    gameName,
                    tagLine,
                    e.statusCode.value()
                )
                return null
            }

            log.warn(
                "Failed to resolve riot account by riot id ({}#{}). status={}, body={}",
                gameName,
                tagLine,
                e.statusCode.value(),
                e.responseBodyAsString
            )
            throw IllegalStateException(
                "Failed to resolve riot account by riot id (status=${e.statusCode.value()}).",
                e
            )
        }
    }

    fun getLeagueEntriesByPuuid(puuid: String): List<RiotLeagueEntryResponse> {
        val trimmedPuuid = puuid.trim()
        require(trimmedPuuid.isNotEmpty()) { "puuid must not be blank." }


        try {
            return leagueRestClient.get()
                .uri("/lol/league/v4/entries/by-puuid/{encryptedPUUID}", trimmedPuuid)
                .retrieve()
                .body(object : ParameterizedTypeReference<List<RiotLeagueEntryResponse>>() {})
                ?: emptyList()

        } catch (e: RestClientResponseException) {
            log.warn(
                "Failed to load league entries by puuid ({}). status={}, body={}",
                trimmedPuuid,
                e.statusCode.value(),
                e.responseBodyAsString
            )
            throw IllegalStateException(
                "Failed to load league entries by puuid (status=${e.statusCode.value()}).",
                e
            )
        }
    }

    data class RiotAccountResponse(
        val puuid: String,
        val gameName: String,
        val tagLine: String
    )

    data class RiotLeagueEntryResponse(
        val leagueId: String,
        val queueType: String,
        val tier: String?,
        val rank: String?,
        val puuid: String,
        val leaguePoints: Int?,
        val wins: Int?,
        val losses: Int?,
    )
}

private fun String.removeAllWhitespace(): String = replace("\\s+".toRegex(), "")

private fun String.containsWhitespace(): Boolean = contains("\\s".toRegex())

private fun buildRiotIdCandidates(value: String): List<String> {
    val trimmed = value.trim()
    val normalized = trimmed.removeAllWhitespace()
    val candidates = linkedSetOf(trimmed, normalized)

    // 공백 없는 입력은 내부적으로 한 칸 공백 삽입 후보를 전부 시도한다.
    if (!trimmed.containsWhitespace() && normalized.length >= 2) {
        for (index in 1 until normalized.length) {
            candidates += normalized.substring(0, index) + " " + normalized.substring(index)
        }
    }

    return candidates.toList()
}
