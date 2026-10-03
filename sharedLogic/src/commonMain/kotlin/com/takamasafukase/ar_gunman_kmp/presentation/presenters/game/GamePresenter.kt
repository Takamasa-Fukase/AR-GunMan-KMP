package com.takamasafukase.ar_gunman_kmp.presentation.presenters.game

import com.takamasafukase.ar_gunman_kmp.deviceInterface.arShootingEngine.ARShootingEngineHandlerInterface
import com.takamasafukase.ar_gunman_kmp.deviceInterface.motionSensor.MotionSensorHandlerInterface
import com.takamasafukase.ar_gunman_kmp.deviceInterface.sound.SoundPlayerInterface
import com.takamasafukase.ar_gunman_kmp.deviceInterface.sound.SoundType
import com.takamasafukase.ar_gunman_kmp.domain.entities.game.GameFlowStatus
import com.takamasafukase.ar_gunman_kmp.domain.entities.game.ReloadingMotionDetectedCountUpdateResult
import com.takamasafukase.ar_gunman_kmp.domain.entities.motion.WeaponControlMotion
import com.takamasafukase.ar_gunman_kmp.domain.entities.weapon.WeaponFireResult
import com.takamasafukase.ar_gunman_kmp.domain.entities.weapon.WeaponReloadStartResult
import com.takamasafukase.ar_gunman_kmp.domain.entities.weapon.WeaponType
import com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces.GameStoreInterface
import com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces.WeaponStoreInterface
import com.takamasafukase.ar_gunman_kmp.domain.useCases.GameFlowDriveUseCaseInterface
import com.takamasafukase.ar_gunman_kmp.domain.useCases.ReloadingMotionCountUpdateUseCaseInterface
import com.takamasafukase.ar_gunman_kmp.domain.useCases.ScoreAddUseCaseInterface
import com.takamasafukase.ar_gunman_kmp.domain.useCases.WeaponChangeUseCaseInterface
import com.takamasafukase.ar_gunman_kmp.domain.useCases.WeaponControlMotionDetectUseCaseInterface
import com.takamasafukase.ar_gunman_kmp.domain.useCases.WeaponFireUseCaseInterface
import com.takamasafukase.ar_gunman_kmp.domain.useCases.WeaponReloadUseCaseInterface
import com.takamasafukase.ar_gunman_kmp.presentation.presenters.game.weaponResources.soundResources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GamePresenter(
    private val coroutineScope: CoroutineScope,
    private val arShootingEngineHandler: ARShootingEngineHandlerInterface,
    private val motionSensorHandler: MotionSensorHandlerInterface,
    private val soundPlayer: SoundPlayerInterface,
    private val gameStore: GameStoreInterface,
    private val weaponStore: WeaponStoreInterface,
    private val weaponFireUseCase: WeaponFireUseCaseInterface,
    private val weaponReloadUseCase: WeaponReloadUseCaseInterface,
    private val weaponChangeUseCase: WeaponChangeUseCaseInterface,
    private val gameFlowDriveUseCase: GameFlowDriveUseCaseInterface,
    private val scoreAddUseCase: ScoreAddUseCaseInterface,
    private val reloadingMotionCountUpdateUseCase: ReloadingMotionCountUpdateUseCaseInterface,
    private val weaponControlMotionDetectUseCase: WeaponControlMotionDetectUseCaseInterface,
) {
    sealed interface OutputEventType {
        object ShowTutorialView : OutputEventType
        object ShowWeaponSelectView : OutputEventType
        object CloseWeaponSelectView : OutputEventType
        data class ShowResultView(val score: Double) : OutputEventType
    }
    val timeCountText: Flow<String> get() = gameStore.timeCount.map { it.countMillisec.timeCountText }
    val currentWeaponType: Flow<WeaponType> get() = weaponStore.weapon.map { it.currentType }
    val bulletsCount: Flow<Int> get() = weaponStore.weapon.map { it.bulletsCount }
    val isWeaponChangeButtonEnabled: Flow<Boolean> get() = gameStore.gameFlow.map { it.status.isTimerRunning }
    val outputEvent: Flow<OutputEventType> get() = _outputEvent.receiveAsFlow()
    private val _outputEvent = Channel<OutputEventType>(Channel.BUFFERED)

    init {
        // FIXME: 暫定対応
        gameFlowDriveUseCase.setScope(scope = coroutineScope)
        weaponReloadUseCase.setScope(scope = coroutineScope)

        arShootingEngineHandler.onEngineReady = {
            println("ログAndroid GamePresenter arShootingEngineSplashCompletion")
            coroutineScope.launch {
                gameFlowDriveUseCase.start()
            }
        }

        arShootingEngineHandler.targetHit = { weaponType ->
            scoreAddUseCase.execute(targetHitPoint = weaponType.targetHitPoint)
            soundPlayer.play(SoundType.TARGET_HIT)
            weaponType.soundResources.bulletHitSound?.let {
                soundPlayer.play(it)
            }
        }

        motionSensorHandler.motionUpdated = motionUpdated@{ motion ->
            // 物理モーションを武器の操作モーションに変換
            val weaponControlMotion = weaponControlMotionDetectUseCase.execute(motion = motion)

            // 武器の操作モーションでは無い場合はreturn
            weaponControlMotion ?: return@motionUpdated

            // 武器の操作モーション種別をハンドリング
            when (weaponControlMotion) {
                WeaponControlMotion.FIRE -> {
                    // 武器の発射
                    coroutineScope.launch {
                        weaponFireUseCase.execute()
                    }
                }

                WeaponControlMotion.RELOAD -> {
                    // 武器のリロード
                    coroutineScope.launch {
                        weaponReloadUseCase.execute()
                    }

                    // リロードモーションの検知回数をカウント
                    val reloadingMotionCountUpdateResult =
                        reloadingMotionCountUpdateUseCase.execute()

                    // リロードモーションの検知回数に応じた結果のハンドリング
                    when (reloadingMotionCountUpdateResult) {
                        ReloadingMotionDetectedCountUpdateResult.NOT_EXCEEDED_LIMIT -> {}
                        ReloadingMotionDetectedCountUpdateResult.EXCEEDED_LIMIT -> {
                            soundPlayer.play(SoundType.TARGET_APPEARANCE_CHANGE)
                            arShootingEngineHandler.changeTargetsAppearance()
                        }
                    }
                }
            }
        }

        coroutineScope.launch {
            weaponFireUseCase.fireResultEvent
                .collect { fireResult ->
                    // 発射結果のハンドリング
                    when (fireResult) {
                        is WeaponFireResult.Success -> {
                            arShootingEngineHandler.renderWeaponFiring()
                            soundPlayer.play(weaponStore.weapon.value.currentType.soundResources.firingSound)
                        }

                        is WeaponFireResult.Failure -> {
                            when (fireResult.reason) {
                                WeaponFireResult.FailureReason.RELOADING -> {}
                                WeaponFireResult.FailureReason.OUT_OF_BULLETS -> {
                                    weaponStore.weapon.value.currentType.soundResources.outOfBulletsSound?.let { outOfBulletsSound ->
                                        soundPlayer.play(outOfBulletsSound)
                                    }
                                }
                            }
                        }
                    }
                }
        }

        coroutineScope.launch {
            weaponReloadUseCase.reloadStartResultEvent
                .collect { reloadStartResult ->
                    // リロード開始結果のハンドリング
                    when (reloadStartResult) {
                        WeaponReloadStartResult.SUCCESS -> {
                            soundPlayer.play(weaponStore.weapon.value.currentType.soundResources.reloadingSound)
                        }

                        WeaponReloadStartResult.FAILURE -> {}
                    }
                }
        }

        coroutineScope.launch {
            gameFlowDriveUseCase.statusStream
                .collect { status ->
                    when (status) {
                        GameFlowStatus.WaitingForTimerStart -> {
                            soundPlayer.play(WeaponType.defaultType.soundResources.appearingSound)
                        }

                        GameFlowStatus.TimerStartedAndWaitingForTimerEnd -> {
                            soundPlayer.play(SoundType.START_WHISTLE)
                            motionSensorHandler.startDetection()
                        }

                        GameFlowStatus.TimerEndedAndWaitingForFlowEnd -> {
                            soundPlayer.play(SoundType.END_WHISTLE)
                            motionSensorHandler.stopDetection()
                            coroutineScope.launch {
                                _outputEvent.send(OutputEventType.CloseWeaponSelectView)
                            }
                        }

                        GameFlowStatus.FlowEnded -> {
                            // 結果画面と名前登録ダイアログの出現音声を再生
                            soundPlayer.play(SoundType.RANKING_APPEAR)
                            coroutineScope.launch {
                                // 結果画面で表示する得点と一緒に遷移指示を流す
                                _outputEvent.send(
                                    OutputEventType.ShowResultView(gameStore.score.value.value)
                                )
                            }
                        }

                        is GameFlowStatus.Blocked -> {
                            when (status.reason) {
                                GameFlowStatus.BlockedReason.TUTORIAL_NOT_COMPLETED -> {
                                    coroutineScope.launch {
                                        _outputEvent.send(OutputEventType.ShowTutorialView)
                                    }
                                }

                                GameFlowStatus.BlockedReason.TIMER_PAUSED -> {}
                            }
                        }

                        GameFlowStatus.FlowNotStarted -> {}
                        GameFlowStatus.TimerResumedAndWaitingForTimerEnd -> {}
                        GameFlowStatus.CheckingTutorialCompletedStatus -> {}
                    }
                }
        }
    }

    fun onViewAppear() {
        gameStore.reset()
        weaponStore.reset()

        arShootingEngineHandler.run()
        arShootingEngineHandler.showWeapon(type = WeaponType.defaultType)
    }

    fun onViewDisappear() {
        arShootingEngineHandler.pause()
    }

    fun weaponChangeButtonTapped() {
        coroutineScope.launch {
            _outputEvent.send(OutputEventType.ShowWeaponSelectView)

            // 武器選択中はタイムカウントの更新を止める
            gameFlowDriveUseCase.pauseTimer()
        }
    }

    fun tutorialEnded() {
        coroutineScope.launch {
            gameFlowDriveUseCase.resolveBlocked()
        }
    }

    fun weaponSelected(weaponType: WeaponType?) {
        val weaponType = weaponType ?: weaponStore.weapon.value.currentType
        weaponChangeUseCase.execute(newType = weaponType)
        arShootingEngineHandler.showWeapon(type = weaponType)
        soundPlayer.play(weaponType.soundResources.appearingSound)

        // タイムカウントの更新を再開する
        coroutineScope.launch {
            gameFlowDriveUseCase.resolveBlocked()
        }
    }
}

private val Int.timeCountText: String
    get() {
        val timeCountMillisec: Int = this

        val seconds = timeCountMillisec / 1000
        val hundredths = (timeCountMillisec % 1000) / 10
        val strTimeCount = "$seconds.${hundredths.toString().padStart(2, '0')}"

        return if (timeCountMillisec < 10000) {
            "0$strTimeCount"
        } else {
            strTimeCount
        }
    }