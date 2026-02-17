package com.garfield.adapter.out.client

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