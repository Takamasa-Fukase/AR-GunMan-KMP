package com.takamasafukase.ar_gunman_kmp.deviceInterface.motionSensor

import com.takamasafukase.ar_gunman_kmp.domain.entities.motion.PhysicalMotion

interface MotionSensorHandlerInterface {
    var motionUpdated: ((PhysicalMotion) -> Unit)?
    fun startDetection()
    fun stopDetection()
}