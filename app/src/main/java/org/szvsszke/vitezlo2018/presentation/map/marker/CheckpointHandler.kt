package org.szvsszke.vitezlo2018.presentation.map.marker

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import java.util.ArrayList

/**
 * Class to show or hide checkpoints on a GoogleMap
 */
class CheckpointHandler(private val markerFactory: CheckpointMarkerFactory,
                        private val markerHandler: MarkerHandler) {

    fun showCheckpoints(map: GoogleMap, checkPoints: List<Checkpoint>) {
        markerHandler.removeMarkers()
        val markerOptions = ArrayList<MarkerOptions>()
        for (checkpoint in checkPoints) {
            markerOptions.add(markerFactory.create(checkpoint))
        }

        markerHandler.addMarkers(map, markerOptions)
    }

    fun hideCheckpoints() {
        markerHandler.removeMarkers()
    }
}
