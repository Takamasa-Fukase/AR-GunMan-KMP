package com.takamasafukase.ar_gunman_kmp.domain.useCases

import com.takamasafukase.ar_gunman_kmp.domain.entities.weapon.WeaponFireResult
import com.takamasafukase.ar_gunman_kmp.domain.entities.weapon.WeaponType
import com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces.WeaponStoreInterface
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface WeaponFireUseCaseInterface {
    val fireResultEvent: SharedFlow<WeaponFireResult>
    suspend fun execute()
}

class WeaponFireUseCase(
    private val weaponStore: WeaponStoreInterface,
    private val weaponReloadUseCase: WeaponReloadUseCaseInterface,
) : WeaponFireUseCaseInterface {
    override val fireResultEvent: SharedFlow<WeaponFireResult> get() = _fireResultEvent.asSharedFlow()
    private val _fireResultEvent = MutableSharedFlow<WeaponFireResult>()

    override suspend fun execute() {
        val (updatedWeapon, fireResult) = weaponStore.weapon.value.fire()
        weaponStore.updateWeapon(value = updatedWeapon)
        _fireResultEvent.emit(fireResult)

        if (fireResult == WeaponFireResult.Success && weaponStore.weapon.value.currentType.reloadType == WeaponType.ReloadType.AUTO) {
            // リロードを自動的に実行
            weaponReloadUseCase.execute()
        }
    }
}