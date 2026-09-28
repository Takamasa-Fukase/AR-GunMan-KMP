package com.takamasafukase.ar_gunman_kmp.deviceInterface.sound

enum class SoundType {
    WESTERN_PISTOL_FIRE,
    PISTOL_APPEAR,
    PISTOL_FIRE,
    PISTOL_OUT_OF_BULLETS,
    PISTOL_RELOAD,
    BAZOOKA_APPEAR,
    BAZOOKA_FIRE,
    BAZOOKA_RELOAD,
    BAZOOKA_EXPLOSION,
    TARGET_HIT,
    TARGET_APPEARANCE_CHANGE,
    START_WHISTLE,
    END_WHISTLE,
    RANKING_APPEAR;

    val needsPlayVibration: Boolean
        get() {
            return this == PISTOL_FIRE || this == BAZOOKA_FIRE
        }
}