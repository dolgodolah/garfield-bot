package com.garfield.adapter.out.client

interface RiotGamesApi {
    fun getRiotAccountByRiotId(gameName: String, tagLine: String): RiotAccountResponse?
    fun getLeagueEntriesByPuuid(puuid: String): List<RiotLeagueEntryResponse>
}
