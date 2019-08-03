package org.szvsszke.vitezlo2018.presentation.map.line

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.szvsszke.vitezlo2018.domain.entity.Point
import org.szvsszke.vitezlo2018.domain.entity.Track
import org.szvsszke.vitezlo2018.presentation.map.marker.MarkerHandler
import org.szvsszke.vitezlo2018.presentation.map.marker.TouristMarkFactory
import javax.inject.Inject

/**
 * Handles drawing to map related to Tourist Paths
 */
class TouristPathHandler @Inject constructor(private val markerFactory: TouristMarkFactory) {

    private var lines = mutableListOf<LineHandler>()
    private var markers = MarkerHandler()

    fun drawPaths(map: GoogleMap, paths: List<Track>) {
        val markerOptions = mutableListOf<MarkerOptions>()
        paths.forEach { track ->
            val trackDescription = track.description ?: ""
            lines.add(LineHandler().apply {
                drawLine(map, track.points.map { LatLng(it.latitude, it.longitude) }, getColor(trackDescription))
            })
            markerOptions.add(markerFactory.create(trackDescription, getMiddle(track.points)))
        }
        markers.addMarkers(map, markerOptions)
    }

    fun removePaths() {
        lines.forEach { it.removeLine() }
        lines.clear()
        markers.removeMarkers()

    }

    private fun getMiddle(points: List<Point>) = points[points.size / 2]

    companion object {

        // FIXME use color preset
        private val ARGB_BLUE = Color.argb(255, 0, 0, 255)
        private val ARGB_RED = Color.argb(255, 255, 0, 0)
        private val ARGB_GREEN = Color.argb(255, 0, 150, 0)
        private val ARGB_YELLOW = Color.argb(255, 255, 255, 0)

        fun getColor(code: String) = when {
            code.startsWith("P") -> ARGB_RED
            code.startsWith("Z") -> ARGB_GREEN
            code.startsWith("K") ->  ARGB_BLUE
            else -> ARGB_YELLOW
        }
    }
}
