package com.takamasafukase.ar_gunman_kmp.presentation.game.weaponResources

import com.takamasafukase.ar_gunman_kmp.deviceInterface.sound.SoundType
import com.takamasafukase.ar_gunman_kmp.domain.entities.weapon.WeaponType

val WeaponType.soundResources: WeaponSoundResources
    get() = when (this) {
        WeaponType.PISTOL -> {
            PistolSoundResources(
                appearingSound = SoundType.PISTOL_APPEAR,
                firingSound = SoundType.PISTOL_FIRE,
                reloadingSound = SoundType.PISTOL_RELOAD,
                outOfBulletsSound = SoundType.PISTOL_OUT_OF_BULLETS,
            )
        }
        WeaponType.BAZOOKA -> {
            BazookaSoundResources(
                appearingSound = SoundType.BAZOOKA_APPEAR,
                firingSound = SoundType.BAZOOKA_FIRE,
                reloadingSound = SoundType.BAZOOKA_RELOAD,
                bulletHitSound = SoundType.BAZOOKA_EXPLOSION,
            )
        }
    }