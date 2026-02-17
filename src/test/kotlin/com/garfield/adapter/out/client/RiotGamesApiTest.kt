package com.garfield.adapter.out.client

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RiotGamesApiTest {
    @Autowired
    private lateinit var riotGamesApi: RiotGamesApiClient

    @Test
    fun getRiotAccountByRiotId() {
        val gameName = "Hide on bush"
        val tagLine = "KR1"

        val riotAccountResponse = riotGamesApi.getRiotAccountByRiotId(gameName, tagLine)

        assertNotNull(riotAccountResponse)
        assertTrue(riotAccountResponse!!.puuid.isNotBlank())
        println(riotAccountResponse)
    }

    @Test
    fun getLeagueEntriesByPuuid() {
        val gameName = "Hide on bush"
        val tagLine = "KR1"
        val riotAccountResponse = riotGamesApi.getRiotAccountByRiotId(gameName, tagLine)

        val leagueEntries = riotGamesApi.getLeagueEntriesByPuuid(riotAccountResponse!!.puuid)

        assertNotNull(leagueEntries)
        assertTrue(leagueEntries.isNotEmpty())
        println(leagueEntries)
    }
}