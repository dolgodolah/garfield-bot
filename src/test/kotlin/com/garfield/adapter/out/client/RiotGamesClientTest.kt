package com.garfield.adapter.out.client

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RiotGamesClientTest {
    @Autowired
    private lateinit var riotGamesClient: RiotGamesClient

    @Test
    fun getPuuidByRiotId() {
        val gameName = "Hide on bush"
        val tagLine = "KR1"

        val puuid = riotGamesClient.getPuuidByRiotId(gameName, tagLine)

        assertNotNull(puuid)
        assertTrue(puuid.isNotBlank())
    }

    @Test
    fun getLeagueEntriesByPuuid() {
        val gameName = "Hide on bush"
        val tagLine = "KR1"
        val puuid = riotGamesClient.getPuuidByRiotId(gameName, tagLine)

        val leagueEntries = riotGamesClient.getLeagueEntriesByPuuid(puuid)

        assertNotNull(leagueEntries)
        assertTrue(leagueEntries.isNotEmpty())
    }
}