package org.szvsszke.vitezlo2018.data.preferences

import android.content.SharedPreferences
import org.szvsszke.vitezlo2018.domain.preferences.InfoBoxPreferences
import org.szvsszke.vitezlo2018.domain.preferences.InfoBoxStatus

class SharedInfoBoxPreferences(private val sharedPreferences: SharedPreferences): InfoBoxPreferences {

    override fun getInfoBoxStatus() = InfoBoxStatus(
            sharedPreferences.getInt(KEY_SELECTED_TRACK_INDEX, 0),
            sharedPreferences.getBoolean(KEY_IS_INFO_EXTENDED, true),
            sharedPreferences.getBoolean(KEY_IS_INFO_LOCKED, false)
    )

    override fun setInfoBoxStatus(infoBoxStatus: InfoBoxStatus) = sharedPreferences.edit().apply {
        putInt(KEY_SELECTED_TRACK_INDEX, infoBoxStatus.selectedTrackIndex)
        putBoolean(KEY_IS_INFO_EXTENDED, infoBoxStatus.isExtended)
        putBoolean(KEY_IS_INFO_LOCKED, infoBoxStatus.isLocked)
    }.apply()

    companion object {
        private const val KEY_SELECTED_TRACK_INDEX = "selectedTrackIndex"
        private const val KEY_IS_INFO_EXTENDED = "infoExtended"
        private const val KEY_IS_INFO_LOCKED = "isHikeLocked"
    }
}