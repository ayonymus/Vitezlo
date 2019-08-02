package org.szvsszke.vitezlo2018.presentation.map.camera

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

fun GoogleMap.centerToArea(low: LatLng, high: LatLng, padding: Int = 80) {
    this.animateCamera(CameraUpdateFactory.newLatLngBounds(
            LatLngBounds(low, high), padding))

}
