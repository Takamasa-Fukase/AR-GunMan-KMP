package com.takamasafukase.ar_gunman_kmp.presentation.presenters.game.weaponResources

import com.takamasafukase.ar_gunman_kmp.deviceInterface.sound.SoundType

interface WeaponSoundResources {
    val appearingSound: SoundType
    val firingSound: SoundType
    val reloadingSound: SoundType
    val outOfBulletsSound: SoundType? get() = null
    val bulletHitSound: SoundType? get() = null
}

data class PistolSoundResources(
    override val appearingSound: SoundType,
    override val firingSound: SoundType,
    override val reloadingSound: SoundType,
    override val outOfBulletsSound: SoundType?
) : WeaponSoundResources

data class BazookaSoundResources(
    override val appearingSound: SoundType,
    override val firingSound: SoundType,
    override val reloadingSound: SoundType,
    override val bulletHitSound: SoundType?
) : WeaponSoundResources