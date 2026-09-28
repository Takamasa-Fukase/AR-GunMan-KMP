package com.takamasafukase.ar_gunman_kmp.data.repositories.stubs

import com.takamasafukase.ar_gunman_kmp.domain.repositoryInterfaces.TutorialRepositoryInterface

class TutorialRepositoryStub : TutorialRepositoryInterface {
    private var isCompleted = false

    override fun getTutorialCompletedFlag(): Boolean {
        return isCompleted
    }

    override fun updateTutorialCompletedFlag(isCompleted: Boolean) {
        this.isCompleted = isCompleted
    }
}