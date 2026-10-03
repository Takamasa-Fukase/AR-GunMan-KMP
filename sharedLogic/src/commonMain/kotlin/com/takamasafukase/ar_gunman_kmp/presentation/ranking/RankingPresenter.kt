package com.takamasafukase.ar_gunman_kmp.presentation.ranking

import com.takamasafukase.ar_gunman_kmp.domain.entities.ranking.RankingItem
import com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces.RankingStoreInterface
import com.takamasafukase.ar_gunman_kmp.domain.useCases.RankingGetUseCaseInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RankingPresenter(
    private val coroutineScope: CoroutineScope,
    private val rankingGetUseCase: RankingGetUseCaseInterface,
    rankingStore: RankingStoreInterface,
) {
    val rankingItems: Flow<List<RankingItem>> = rankingStore.ranking.map { it?.items ?: emptyList() }

    fun onViewAppear() {
        getRanking()
    }

    // MARK: - Private Methods
    private fun getRanking() {
        try {
            coroutineScope.launch {
                rankingGetUseCase.execute()
            }

        } catch (error: Exception) {
            println("ログAndroid: RankingVM getRanking error: $error")
        }
    }
}