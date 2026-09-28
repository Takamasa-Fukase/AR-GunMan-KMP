package com.takamasafukase.ar_gunman_kmp.domain.useCases

import com.takamasafukase.ar_gunman_kmp.domain.entities.weapon.WeaponType
import com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces.WeaponStoreInterface

interface WeaponChangeUseCaseInterface {
    fun execute(newType: WeaponType)
}

class WeaponChangeUseCase(
    private val weaponStore: WeaponStoreInterface,
    private val weaponReloadUseCase: WeaponReloadUseCaseInterface,
) : WeaponChangeUseCaseInterface {
    override fun execute(newType: WeaponType) {
        // 既存のリロードをキャンセルする
        weaponReloadUseCase.stopCurrentReloadIfExists()
        weaponStore.updateWeapon(
            value = weaponStore.weapon.value.change(newType = newType)
        )
    }
}