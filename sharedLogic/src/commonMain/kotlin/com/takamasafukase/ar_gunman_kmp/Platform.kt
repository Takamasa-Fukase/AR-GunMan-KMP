package com.takamasafukase.ar_gunman_kmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform