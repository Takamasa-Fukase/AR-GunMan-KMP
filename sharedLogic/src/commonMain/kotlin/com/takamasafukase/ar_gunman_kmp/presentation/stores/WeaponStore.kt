package com.takamasafukase.ar_gunman_kmp.presentation.stores

import com.takamasafukase.ar_gunman_kmp.domain.entities.weapon.Weapon
import com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces.WeaponStoreInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object WeaponStore : WeaponStoreInterface {
    override val weapon: StateFlow<Weapon> get() = _weapon.asStateFlow()
    private val _weapon = MutableStateFlow(value = Weapon())

    override fun updateWeapon(value: Weapon) {
        _weapon.value = value
    }

    override fun reset() {
        _weapon.value = Weapon()
    }
}