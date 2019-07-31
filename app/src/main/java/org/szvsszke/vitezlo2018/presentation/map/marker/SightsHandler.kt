package org.szvsszke.vitezlo2018.presentation.map.marker

import com.google.android.gms.maps.GoogleMap
import org.szvsszke.vitezlo2018.domain.entity.Sight
import javax.inject.Inject

class SightsHandler @Inject constructor(private val markerHandler: MarkerHandler,
                                        private val sightMarkerFactory: SightMarkerFactory) {

    fun showSights(map: GoogleMap, sights: List<Sight>) {
        markerHandler.removeMarkers()
        markerHandler.addMarkers(map, sights.map { sight -> sightMarkerFactory.create(sight) })
    }

    fun hideSights() {
        markerHandler.removeMarkers()
    }

}
