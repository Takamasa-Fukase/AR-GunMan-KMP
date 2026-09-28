package com.takamasafukase.ar_gunman_kmp.data.models

import com.takamasafukase.ar_gunman_kmp.domain.entities.ranking.RankingItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RankingItemDto(
    val score: Double = 0.0,

    @SerialName("user_name")
    val userName: String = "",
) {
    fun toDomain(): RankingItem {
        return RankingItem(
            score = score,
            userName = userName,
        )
    }
}

fun RankingItem.toDto(): RankingItemDto {
    return RankingItemDto(
        score = score,
        userName = userName,
    )
}