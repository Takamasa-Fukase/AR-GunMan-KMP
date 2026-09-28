package com.takamasafukase.ar_gunman_kmp.deviceInterface.arShootingEngine

import com.takamasafukase.ar_gunman_kmp.domain.entities.weapon.WeaponType

interface ARShootingEngineHandlerInterface {
    var onEngineReady: (() -> Unit)?
    var targetHit: ((WeaponType) -> Unit)?
    fun run()
    fun pause()
    fun showWeapon(type: WeaponType)
    fun renderWeaponFiring()
    fun changeTargetsAppearance()
}