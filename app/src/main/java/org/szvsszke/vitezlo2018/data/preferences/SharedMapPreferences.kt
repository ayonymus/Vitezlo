package org.szvsszke.vitezlo2018.data.preferences

import android.content.SharedPreferences
import org.szvsszke.vitezlo2018.domain.entity.Point
import org.szvsszke.vitezlo2018.domain.preferences.MapPreferences
import org.szvsszke.vitezlo2018.domain.preferences.MapStatus
import javax.inject.Inject

class SharedMapPreferences @Inject constructor(private val sharedPreferences: SharedPreferences): MapPreferences {

    override fun getMapStatus() = MapStatus(
                sharedPreferences.getInt(KEY_MAP_TYPE, DEFAUL_MAP),
                Point(Double.fromBits(sharedPreferences.getLong(KEY_LAST_CAMERA_LAT, DEFAULT_LAT.toRawBits())),
                        Double.fromBits(sharedPreferences.getLong(KEY_LAST_CAMERA_LON, DEFAULT_LONG.toRawBits()))),
                sharedPreferences.getFloat(KEY_LAST_CAMERA_ZOOM, DEFAULT_ZOOM))

    override fun setMapStatus(status: MapStatus) = sharedPreferences.edit().apply {
            putInt(KEY_MAP_TYPE, status.mapType)
            putLong(KEY_LAST_CAMERA_LAT, status.cameraPosition.latitude.toRawBits())
            putLong(KEY_LAST_CAMERA_LON, status.cameraPosition.longitude.toRawBits())
            putFloat(KEY_LAST_CAMERA_ZOOM, status.cameraZoom)
        }.apply()

    companion object {
        private const val KEY_MAP_TYPE = "map_type"
        private const val KEY_LAST_CAMERA_LAT = "lat"
        private const val KEY_LAST_CAMERA_LON = "lon"
        private const val KEY_LAST_CAMERA_ZOOM = "zoom"

        private const val DEFAUL_MAP = 1
        private const val DEFAULT_ZOOM = 14F
        private const val DEFAULT_LAT = 48.409291847117601
        private const val DEFAULT_LONG = 20.724328984580993

    }
}
