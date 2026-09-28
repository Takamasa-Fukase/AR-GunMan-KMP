package com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces

import com.takamasafukase.ar_gunman_kmp.domain.entities.game.GameFlow
import com.takamasafukase.ar_gunman_kmp.domain.entities.game.GameScore
import com.takamasafukase.ar_gunman_kmp.domain.entities.game.GameTimeCount
import com.takamasafukase.ar_gunman_kmp.domain.entities.game.ReloadingMotionDetectedCount
import kotlinx.coroutines.flow.StateFlow

interface GameStoreInterface {
    val gameFlow: StateFlow<GameFlow>
    val timeCount: StateFlow<GameTimeCount>
    val score: StateFlow<GameScore>
    val reloadingMotionDetectedCount: StateFlow<ReloadingMotionDetectedCount>
    fun updateGameFlow(value: GameFlow)
    fun updateTimeCount(value: (GameTimeCount))
    fun updateScore(value: GameScore)
    fun updateReloadingMotionDetectedCount(value: ReloadingMotionDetectedCount)
    fun reset()
}