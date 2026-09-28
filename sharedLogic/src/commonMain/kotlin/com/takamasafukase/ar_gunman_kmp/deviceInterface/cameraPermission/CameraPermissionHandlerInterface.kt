package com.takamasafukase.ar_gunman_kmp.deviceInterface.cameraPermission

interface CameraPermissionHandlerInterface {
    fun getCameraUsagePermissionGrantedFlag(): Boolean
    fun requestCameraUsagePermission()
}