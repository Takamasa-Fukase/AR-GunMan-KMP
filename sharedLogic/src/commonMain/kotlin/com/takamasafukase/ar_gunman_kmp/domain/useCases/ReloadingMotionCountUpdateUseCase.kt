package com.takamasafukase.ar_gunman_kmp.domain.useCases

import com.takamasafukase.ar_gunman_kmp.domain.entities.game.ReloadingMotionDetectedCountUpdateResult
import com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces.GameStoreInterface

interface ReloadingMotionCountUpdateUseCaseInterface {
    fun execute(): ReloadingMotionDetectedCountUpdateResult
}

class ReloadingMotionCountUpdateUseCase(
    private var gameStore: GameStoreInterface
) : ReloadingMotionCountUpdateUseCaseInterface {
    override fun execute(): ReloadingMotionDetectedCountUpdateResult {
        val (updatedCount, result) = gameStore.reloadingMotionDetectedCount.value.update()
        gameStore.updateReloadingMotionDetectedCount(value = updatedCount)
        return result
    }
}