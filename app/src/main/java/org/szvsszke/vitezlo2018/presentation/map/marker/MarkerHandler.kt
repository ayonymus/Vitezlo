package org.szvsszke.vitezlo2018.presentation.map.marker

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import javax.inject.Inject

/**
 * Class to display and cache visible Markers on a GoogleMap
 */
class MarkerHandler @Inject constructor() {

    private val visibleMarkers: ArrayList<Marker> = ArrayList()

    fun addMarkers(map: GoogleMap, markerOptions: List<MarkerOptions>) {
        markerOptions.forEach { markerOption ->
            visibleMarkers.add(map.addMarker(markerOption))
        }
    }

    fun removeMarkers() {
        visibleMarkers.forEach { it.remove() }
        visibleMarkers.clear()
    }
}
