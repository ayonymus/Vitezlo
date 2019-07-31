package org.szvsszke.vitezlo2018.presentation.map.marker

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.szvsszke.vitezlo2018.domain.entity.Sight
import javax.inject.Inject

class SightMarkerFactory @Inject constructor() {

    fun create(sight: Sight): MarkerOptions {
        val icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        return MarkerOptions()
                .position(LatLng(sight.latitude, sight.longitude))
                .title(sight.name)
                .snippet(sight.link)
                .icon(icon)
    }

}
