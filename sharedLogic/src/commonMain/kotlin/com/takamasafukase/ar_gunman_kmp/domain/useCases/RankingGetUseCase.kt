package com.takamasafukase.ar_gunman_kmp.domain.useCases

import com.takamasafukase.ar_gunman_kmp.domain.entities.ranking.Ranking
import com.takamasafukase.ar_gunman_kmp.domain.repositoryInterfaces.RankingRepositoryInterface
import com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces.RankingStoreInterface

interface RankingGetUseCaseInterface {
    suspend fun execute()
}

class RankingGetUseCase(
    private val rankingRepository: RankingRepositoryInterface,
    private val rankingStore: RankingStoreInterface,
) : RankingGetUseCaseInterface {
    override suspend fun execute() {
        val items = rankingRepository.getItems()
        rankingStore.updateRanking(value = Ranking(items = items))
    }
}