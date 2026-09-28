package com.takamasafukase.ar_gunman_kmp.data.repositories

import com.takamasafukase.ar_gunman_kmp.data.dataSources.FirestoreClientInterface
import com.takamasafukase.ar_gunman_kmp.data.dataSources.FirestoreConst
import com.takamasafukase.ar_gunman_kmp.data.dataSources.addItem
import com.takamasafukase.ar_gunman_kmp.data.dataSources.getItems
import com.takamasafukase.ar_gunman_kmp.data.models.RankingItemDto
import com.takamasafukase.ar_gunman_kmp.data.models.toDto
import com.takamasafukase.ar_gunman_kmp.domain.entities.ranking.RankingItem
import com.takamasafukase.ar_gunman_kmp.domain.repositoryInterfaces.RankingRepositoryInterface

class RankingRepository(
    private val firestoreClient: FirestoreClientInterface
) : RankingRepositoryInterface {
    override suspend fun getItems(): List<RankingItem> {
        return firestoreClient
            .getItems<RankingItemDto>(
                collectionPath = FirestoreConst.WORLD_RANKING
            )
            .map { it.toDomain() }
    }

    override suspend fun registerItem(item: RankingItem) {
        firestoreClient
            .addItem(
                collectionPath = FirestoreConst.WORLD_RANKING,
                request = item.toDto()
            )
    }
}