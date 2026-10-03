package com.takamasafukase.ar_gunman_kmp.presentation.presenters.nameRegister

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.takamasafukase.ar_gunman_kmp.domain.entities.ranking.Ranking
import com.takamasafukase.ar_gunman_kmp.domain.entities.ranking.RankingItem
import com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces.RankingStoreInterface
import com.takamasafukase.ar_gunman_kmp.domain.useCases.RankingRegisterUseCaseInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow

class NameRegisterViewModel(
    val score: Double,
    private val coroutineScope: CoroutineScope,
    private val rankingRegisterUseCase: RankingRegisterUseCaseInterface,
    private val rankingStore: RankingStoreInterface,
) {
    sealed interface OutputEventType {
        data class Close(val registeredRankingItem: RankingItem? = null) : OutputEventType
    }

    val temporaryRankText: Flow<String?> get() = rankingStore.ranking.map { makeTemporaryRankText(it) }
    val nameInputText: StateFlow<String> get() = _nameInputText.asStateFlow()
    val isRegistering: StateFlow<Boolean> get() = _isRegistering.asStateFlow()
    val outputEvent: Flow<OutputEventType> get() = _outputEvent.receiveAsFlow()

    private val _nameInputText = MutableStateFlow(value = "")
    private val _isRegistering = MutableStateFlow(value = false)
    private val _outputEvent = Channel<OutputEventType>(Channel.BUFFERED)

    fun onChangeNameText(text: String) {
        _nameInputText.value = text
    }

    fun onTapNoThanksButton() {
        coroutineScope.launch {
            _outputEvent.send(OutputEventType.Close())
        }
    }

    fun onTapRegisterButton() {
        // 名前未入力の場合は弾く
        if (_nameInputText.value.isEmpty()) {
            return
        }

        // ボタン上にインジケータ表示
        _isRegistering.value = true

        // 入力された名前とスコアで新しいランキングを作成
        val newRankingItem = RankingItem(
            userName = _nameInputText.value,
            score = score,
        )

        // 登録
        try {
            coroutineScope.launch {
                rankingRegisterUseCase.execute(item = newRankingItem)
                // 今回登録したランキングデータと一緒にダイアログを閉じる指示を流す
                _outputEvent.send(OutputEventType.Close(registeredRankingItem = newRankingItem))
            }

        } catch (error: Exception) {
            println("ログAndroid: RankingVM getRanking error: $error")
        }
    }

    // MARK: - Private Methods
    private fun makeTemporaryRankText(ranking: Ranking?): String? {
        // ランキング取得中の場合はrankingがnilなのでnilを返す
        val _ranking = ranking ?: return null
        // 今回のscoreで仮に登録した場合の順位
        val temporaryRank = _ranking.getTentativeRankIndex(score = score) + 1
        // 登録済みランキング数に今回の結果を加えた数
        val totalCount = _ranking.items.size + 1
        return "$temporaryRank / $totalCount"
    }
}