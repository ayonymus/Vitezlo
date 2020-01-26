package org.szvsszke.vitezlo2018.domain.preferences

import org.szvsszke.vitezlo2018.domain.entity.Point

/**
 * Map status go here
 */
interface MapPreferences {
    fun getMapStatus(): MapStatus
    fun setMapStatus(status: MapStatus)

    fun getInfoBoxStatus(): InfoBoxStatus
    fun setInfoBoxStatus(infoBoxStatus: InfoBoxStatus)
}

data class MapStatus(
        val mapType: Int,
        val cameraPosition: Point,
        val cameraZoom: Float)

data class InfoBoxStatus(
        val selectedTrackIndex: Int,
        val isExtended: Boolean,
        val isLocked: Boolean)