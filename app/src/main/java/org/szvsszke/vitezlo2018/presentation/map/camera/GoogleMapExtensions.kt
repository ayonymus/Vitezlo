package org.szvsszke.vitezlo2018.presentation.map.camera

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import org.szvsszke.vitezlo2018.domain.entity.Point
import org.szvsszke.vitezlo2018.domain.preferences.MapStatus

fun GoogleMap.centerToArea(low: LatLng, high: LatLng, padding: Int = 80) {
    this.animateCamera(CameraUpdateFactory.newLatLngBounds(
            LatLngBounds(low, high), padding))
}

fun Point.toLatLng() = LatLng(this.latitude, this.longitude)

fun LatLng.toPoint() = Point(this.latitude, this.longitude)

fun GoogleMap.getStatus() = MapStatus(
        mapType,
        cameraPosition.target.toPoint(),
        cameraPosition.zoom
)

fun GoogleMap?.switchType() {
    this?.apply {
        mapType = when (mapType) {
            GoogleMap.MAP_TYPE_NORMAL -> GoogleMap.MAP_TYPE_HYBRID
            GoogleMap.MAP_TYPE_HYBRID -> GoogleMap.MAP_TYPE_TERRAIN
            else -> GoogleMap.MAP_TYPE_NORMAL
        }
    }
}