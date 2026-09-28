package com.takamasafukase.ar_gunman_kmp.domain.repositoryInterfaces

import com.takamasafukase.ar_gunman_kmp.domain.entities.ranking.RankingItem

interface RankingRepositoryInterface {
    suspend fun getItems(): List<RankingItem>
    suspend fun registerItem(item: RankingItem)
}