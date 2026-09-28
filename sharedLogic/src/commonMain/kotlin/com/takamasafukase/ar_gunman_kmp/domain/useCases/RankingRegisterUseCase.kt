package com.takamasafukase.ar_gunman_kmp.domain.useCases

import com.takamasafukase.ar_gunman_kmp.domain.entities.ranking.RankingItem
import com.takamasafukase.ar_gunman_kmp.domain.repositoryInterfaces.RankingRepositoryInterface
import com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces.RankingStoreInterface

interface RankingRegisterUseCaseInterface {
    suspend fun execute(item: RankingItem)
}

class RankingRegisterUseCase(
    private val rankingRepository: RankingRepositoryInterface,
    private val rankingStore: RankingStoreInterface,
) : RankingRegisterUseCaseInterface {
    override suspend fun execute(item: RankingItem) {
        rankingRepository.registerItem(item = item)
        rankingStore.updateRanking(
            value = rankingStore.ranking.value?.insertRegisteredRanking(item = item)
        )
    }
}