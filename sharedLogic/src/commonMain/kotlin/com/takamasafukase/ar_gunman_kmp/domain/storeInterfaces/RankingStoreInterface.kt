package com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces

import com.takamasafukase.ar_gunman_kmp.domain.entities.ranking.Ranking
import kotlinx.coroutines.flow.StateFlow

interface RankingStoreInterface {
    val ranking: StateFlow<Ranking?>
    fun updateRanking(value: Ranking?)
    fun reset()
}