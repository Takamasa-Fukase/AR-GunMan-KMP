package com.takamasafukase.ar_gunman_kmp.domain.useCases

import com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces.GameStoreInterface

interface ScoreAddUseCaseInterface {
    fun execute(targetHitPoint: Int)
}

class ScoreAddUseCase(
    private val gameStore: GameStoreInterface
) : ScoreAddUseCaseInterface {
    override fun execute(targetHitPoint: Int) {
        gameStore.updateScore(
            value = gameStore.score.value.add(targetHitPoint = targetHitPoint)
        )
    }
}