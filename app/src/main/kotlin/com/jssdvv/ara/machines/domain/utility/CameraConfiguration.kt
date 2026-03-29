package com.jssdvv.ara.machines.domain.utility

import com.google.ar.core.Config
import com.google.ar.core.Session

fun configureARSession(session: Session, config: Config) {
    config.apply{
        setFocusMode(Config.FocusMode.AUTO)
        setLightEstimationMode(Config.LightEstimationMode.DISABLED)
        setInstantPlacementMode(Config.InstantPlacementMode.DISABLED)
        setDepthMode(
            when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                true -> Config.DepthMode.AUTOMATIC
                else -> Config.DepthMode.DISABLED
            }
        )
    }
}