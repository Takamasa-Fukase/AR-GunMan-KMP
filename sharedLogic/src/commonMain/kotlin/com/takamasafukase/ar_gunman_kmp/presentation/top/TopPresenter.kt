package com.takamasafukase.ar_gunman_kmp.presentation.top

import com.takamasafukase.ar_gunman_kmp.deviceInterface.cameraPermission.CameraPermissionHandlerInterface
import com.takamasafukase.ar_gunman_kmp.deviceInterface.sound.SoundPlayerInterface
import com.takamasafukase.ar_gunman_kmp.deviceInterface.sound.SoundType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TopPresenter(
    private val coroutineScope: CoroutineScope,
    private val cameraPermissionHandler: CameraPermissionHandlerInterface,
    private val soundPlayer: SoundPlayerInterface,
) {
    enum class OutputEventType {
        SHOW_GAME_VIEW,
        SHOW_TUTORIAL_VIEW,
        SHOW_SETTINGS_VIEW,
        SHOW_DEVICE_SETTINGS;
    }
    sealed class IconButtonType {
        object Start : IconButtonType()
        object Settings : IconButtonType()
        object HowToPlay : IconButtonType()
    }

    val isStartButtonIconSwitched: StateFlow<Boolean> get() = _isStartButtonIconSwitched.asStateFlow()
    val isSettingsButtonIconSwitched: StateFlow<Boolean> get() = _isSettingsButtonIconSwitched.asStateFlow()
    val isHowToPlayButtonIconSwitched: StateFlow<Boolean> get() = _isHowToPlayButtonIconSwitched.asStateFlow()
    val isPermissionDescriptionDialogPresented: StateFlow<Boolean> get() = _isPermissionDescriptionDialogPresented.asStateFlow()
    val outputEvent: Flow<OutputEventType> get() = _outputEvent.receiveAsFlow()

    private val _isStartButtonIconSwitched = MutableStateFlow(value = false)
    private val _isSettingsButtonIconSwitched = MutableStateFlow(value = false)
    private val _isHowToPlayButtonIconSwitched = MutableStateFlow(value = false)
    private val _isPermissionDescriptionDialogPresented = MutableStateFlow(value = false)
    private val _outputEvent = Channel<OutputEventType>(Channel.BUFFERED)


    fun onViewAppear() {
        cameraPermissionHandler.requestCameraUsagePermission()
    }

    fun onTapStartButton() {
        switchButtonIconAndRevert(type = IconButtonType.Start)
    }

    fun onTapSettingsButton() {
        switchButtonIconAndRevert(type = IconButtonType.Settings)
    }

    fun onTapHowToPlayButton() {
        switchButtonIconAndRevert(type = IconButtonType.HowToPlay)
    }

    fun onTapConfirmButtonOfPermissionDescriptionDialog() {
        _isPermissionDescriptionDialogPresented.value = false
        coroutineScope.launch {
            _outputEvent.send(OutputEventType.SHOW_DEVICE_SETTINGS)
        }
    }

    fun onClosePermissionDescriptionDialog() {
        _isPermissionDescriptionDialogPresented.value = false
    }

    // MARK: - Private Methods
    private fun switchButtonIconAndRevert(type: IconButtonType) {
        // ウエスタン風な銃声の再生
        soundPlayer.play(SoundType.WESTERN_PISTOL_FIRE)
        // 対象のボタンに弾痕の画像を表示
        when (type) {
            IconButtonType.Start -> {
                _isStartButtonIconSwitched.value = true
            }
            IconButtonType.Settings -> {
                _isSettingsButtonIconSwitched.value = true
            }
            IconButtonType.HowToPlay -> {
                _isHowToPlayButtonIconSwitched.value = true
            }
        }
        coroutineScope.launch {
            // 0.5秒待機
            delay(timeMillis = 500)

            // 画像を元の的に戻す
            _isStartButtonIconSwitched.value = false
            _isSettingsButtonIconSwitched.value = false
            _isHowToPlayButtonIconSwitched.value = false

            // 対象のボタンごとの遷移指示を流す
            when (type) {
                IconButtonType.Start -> {
                    val isCameraPermissionGranted = cameraPermissionHandler.getCameraUsagePermissionGrantedFlag()
                    if (isCameraPermissionGranted) {
                        _outputEvent.send(OutputEventType.SHOW_GAME_VIEW)
                    } else {
                        _isPermissionDescriptionDialogPresented.value = true
                    }
                }
                IconButtonType.Settings -> {
                    _outputEvent.send(OutputEventType.SHOW_SETTINGS_VIEW)
                }
                IconButtonType.HowToPlay -> {
                    _outputEvent.send(OutputEventType.SHOW_TUTORIAL_VIEW)
                }
            }
        }
    }
}