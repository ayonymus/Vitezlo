package org.szvsszke.vitezlo2018.domain.preferences

interface InfoBoxPreferences {
    fun getInfoBoxStatus(): InfoBoxStatus
    fun setInfoBoxStatus(infoBoxStatus: InfoBoxStatus)
}

data class InfoBoxStatus(
        val selectedTrackIndex: Int,
        val isExtended: Boolean,
        val isLocked: Boolean)