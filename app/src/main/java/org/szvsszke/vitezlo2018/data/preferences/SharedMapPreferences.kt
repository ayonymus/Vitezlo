package org.szvsszke.vitezlo2018.data.preferences

import android.content.SharedPreferences
import org.szvsszke.vitezlo2018.domain.entity.Point
import org.szvsszke.vitezlo2018.domain.preferences.InfoBoxStatus
import org.szvsszke.vitezlo2018.domain.preferences.MapPreferences
import org.szvsszke.vitezlo2018.domain.preferences.MapStatus
import javax.inject.Inject

class SharedMapPreferences @Inject constructor(private val sharedPreferences: SharedPreferences): MapPreferences {

    override fun getMapStatus() = MapStatus(
                sharedPreferences.getInt(KEY_MAP_TYPE, 1),
                Point(Double.fromBits(sharedPreferences.getLong(KEY_LAST_CAMERA_LAT, 0)),
                        Double.fromBits(sharedPreferences.getLong(KEY_LAST_CAMERA_LON, 0))),
                sharedPreferences.getFloat(KEY_LAST_CAMERA_ZOOM, 0F))

    override fun setMapStatus(status: MapStatus) = sharedPreferences.edit().apply {
            putInt(KEY_MAP_TYPE, status.mapType)
            putLong(KEY_LAST_CAMERA_LAT, status.cameraPosition.latitude.toRawBits())
            putLong(KEY_LAST_CAMERA_LON, status.cameraPosition.longitude.toRawBits())
            putFloat(KEY_LAST_CAMERA_ZOOM, status.cameraZoom)
        }.apply()

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
        private const val KEY_MAP_TYPE = "map_type"
        private const val KEY_LAST_CAMERA_LAT = "lat"
        private const val KEY_LAST_CAMERA_LON = "lon"
        private const val KEY_LAST_CAMERA_ZOOM = "zoom"

        private const val KEY_SELECTED_TRACK_INDEX = "selectedTrackIndex"
        private const val KEY_IS_INFO_EXTENDED = "infoExtended"
        private const val KEY_IS_INFO_LOCKED = "isHikeLocked"
    }
}
