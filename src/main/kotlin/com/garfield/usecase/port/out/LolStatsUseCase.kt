package com.garfield.usecase.port.out

import com.garfield.domain.LolStats

interface LolStatsUseCase {
    fun loadLolStats(summonerName: String, tagLine: String): LolStats?
}