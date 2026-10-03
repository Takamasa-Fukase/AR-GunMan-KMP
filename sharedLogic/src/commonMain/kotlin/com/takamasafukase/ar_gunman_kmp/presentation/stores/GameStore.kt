package com.takamasafukase.ar_gunman_kmp.presentation.stores

import com.takamasafukase.ar_gunman_kmp.domain.entities.game.GameFlow
import com.takamasafukase.ar_gunman_kmp.domain.entities.game.GameScore
import com.takamasafukase.ar_gunman_kmp.domain.entities.game.GameTimeCount
import com.takamasafukase.ar_gunman_kmp.domain.entities.game.ReloadingMotionDetectedCount
import com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces.GameStoreInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object GameStore : GameStoreInterface {
    override val gameFlow: StateFlow<GameFlow> get() = _gameFlow.asStateFlow()
    override val timeCount: StateFlow<GameTimeCount> get() = _timeCount.asStateFlow()
    override val score: StateFlow<GameScore> get() = _score.asStateFlow()
    override val reloadingMotionDetectedCount: StateFlow<ReloadingMotionDetectedCount> get() = _reloadingMotionDetectedCount.asStateFlow()

    private val _gameFlow = MutableStateFlow(value = GameFlow())
    private val _timeCount = MutableStateFlow(value = GameTimeCount())
    private val _score = MutableStateFlow(value = GameScore())
    private val _reloadingMotionDetectedCount = MutableStateFlow(value = ReloadingMotionDetectedCount())

    override fun updateGameFlow(value: GameFlow) {
        _gameFlow.value = value
    }

    override fun updateTimeCount(value: GameTimeCount) {
        _timeCount.value = value
    }

    override fun updateScore(value: GameScore) {
        _score.value = value
    }

    override fun updateReloadingMotionDetectedCount(value: ReloadingMotionDetectedCount) {
        _reloadingMotionDetectedCount.value = value
    }

    override fun reset() {
        _gameFlow.value = GameFlow()
        _timeCount.value = GameTimeCount()
        _score.value = GameScore()
        _reloadingMotionDetectedCount.value = ReloadingMotionDetectedCount()
    }
}