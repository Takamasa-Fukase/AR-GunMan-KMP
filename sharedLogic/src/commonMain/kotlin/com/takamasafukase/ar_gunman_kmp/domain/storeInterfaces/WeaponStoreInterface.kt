package com.takamasafukase.ar_gunman_kmp.domain.storeInterfaces

import com.takamasafukase.ar_gunman_kmp.domain.entities.weapon.Weapon
import kotlinx.coroutines.flow.StateFlow

interface WeaponStoreInterface {
    val weapon: StateFlow<Weapon>
    fun updateWeapon(value: Weapon)
    fun reset()
}