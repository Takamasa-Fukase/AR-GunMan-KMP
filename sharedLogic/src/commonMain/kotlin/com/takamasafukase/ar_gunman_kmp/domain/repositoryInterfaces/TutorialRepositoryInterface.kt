package com.takamasafukase.ar_gunman_kmp.domain.repositoryInterfaces

interface TutorialRepositoryInterface {
    fun getTutorialCompletedFlag(): Boolean
    fun updateTutorialCompletedFlag(isCompleted: Boolean)
}