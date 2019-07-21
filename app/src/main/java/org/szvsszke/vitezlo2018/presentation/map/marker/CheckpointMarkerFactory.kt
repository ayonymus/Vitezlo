package org.szvsszke.vitezlo2018.presentation.map.marker

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions;
import org.szvsszke.vitezlo2018.domain.Checkpoint
import org.szvsszke.vitezlo2018.domain.MappingRepository

class CheckpointMarkerFactory(private val iconRepository: MappingRepository<String, BitmapDescriptor>) {

    fun create(checkpoint: Checkpoint) = MarkerOptions()
            .position(LatLng(checkpoint.latitude, checkpoint.longitude))
            .title(checkpoint.name)
            .icon(iconRepository.getData(checkpoint.name))
}
