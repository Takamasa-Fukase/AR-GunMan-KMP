package com.takamasafukase.ar_gunman_kmp.presentation.result

import com.takamasafukase.ar_gunman_kmp.deviceInterface.sound.SoundPlayerInterface
import com.takamasafukase.ar_gunman_kmp.domain.entities.ranking.RankingItem
import com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces.RankingStoreInterface
import com.takamasafukase.ar_gunman_kmp.domain.useCases.RankingGetUseCaseInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ResultPresenter(
    val score: Double,
    private val coroutineScope: CoroutineScope,
    private val soundPlayer: SoundPlayerInterface,
    private val rankingGetUseCase: RankingGetUseCaseInterface,
    private val rankingStore: RankingStoreInterface,
) {
    data class UIState(
        val rankingItems: List<RankingItem> = emptyList(),
        val isButtonsVisible: Boolean = false,
        val rankingListHighlightedIndex: Int? = null,
    )
    sealed interface OutputEventType {
        data class ShowNameRegisterView(val score: Double) : OutputEventType
        data class ShowRegisteredRankingItem(val index: Int) : OutputEventType
    }

    val rankingItems: Flow<List<RankingItem>> = rankingStore.ranking.map { it?.items ?: emptyList() }
    val isButtonsVisible: StateFlow<Boolean> get() = _isButtonsVisibleFlow.asStateFlow()
    val outputEvent: Flow<OutputEventType> get() = _outputEvent.receiveAsFlow()

    private val _isButtonsVisibleFlow = MutableStateFlow(value = false)
    private val _outputEvent = Channel<OutputEventType>(Channel.BUFFERED)

    fun onViewAppear() {
        getRanking()

        coroutineScope.launch {
            // 0.5秒後に名前登録ダイアログを表示させる指示を流す
            delay(timeMillis = 500)
            _outputEvent.send(OutputEventType.ShowNameRegisterView(score = score))
        }
    }

    fun onCloseNameRegisterDialog(registeredRankingItem: RankingItem?) {
        coroutineScope.launch {
            // 0.1秒後にボタンの出現アニメーションを開始させる
            delay(timeMillis = 100)
            _isButtonsVisibleFlow.value = true
        }

        // 受け取ったランキングデータがnullじゃ無い場合（ユーザーが登録をした）の処理
        if (registeredRankingItem != null) {
            val rankIndex = rankingStore.ranking.value?.getTentativeRankIndex(
                score = registeredRankingItem.score
            ) ?: 0

            // 新たに登録された今回の結果データをランキングリスト上で表示させる
            coroutineScope.launch {
                _outputEvent.send(OutputEventType.ShowRegisteredRankingItem(index = rankIndex))
            }
        }
    }

    // MARK: - Private Methods
    private fun getRanking() {
        try {
            coroutineScope.launch {
                rankingGetUseCase.execute()
            }

        } catch (error: Exception) {
            println("ログAndroid: ResultVM getRanking error: $error")
        }
    }
}