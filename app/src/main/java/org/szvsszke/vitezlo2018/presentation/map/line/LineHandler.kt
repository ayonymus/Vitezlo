package org.szvsszke.vitezlo2018.presentation.map.line

import androidx.annotation.ColorInt
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import javax.inject.Inject

/**
 * Class to handle drawing a single line on a GoogleMap view
 */
class LineHandler @Inject constructor() {

    private var visibleLine: Polyline? = null

    fun drawLine(map: GoogleMap, line: List<LatLng>, @ColorInt color: Int) {
        visibleLine?.remove()
        visibleLine = map.addPolyline(PolylineOptions()
                .geodesic(true)
                .addAll(line)
                .color(color)
                .zIndex(DEFAULT_Z)
                .width(DEFAULT_WIDTH))
    }

    fun removeLine() {
        visibleLine?.remove()
    }

    companion object {
        private const val DEFAULT_Z = 10F
        private const val DEFAULT_WIDTH = 10F
    }
}
