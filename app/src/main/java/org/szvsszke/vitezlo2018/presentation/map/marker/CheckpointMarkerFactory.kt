package org.szvsszke.vitezlo2018.presentation.map.marker

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions;
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import org.szvsszke.vitezlo2018.domain.MappingRepository
import javax.inject.Inject

class CheckpointMarkerFactory @Inject constructor(
        private val iconRepository: MappingRepository<String, BitmapDescriptor>) {

    // TODO does it ever fail?
    fun create(checkpoint: Checkpoint): MarkerOptions {
        val icon = when (val iconResponse = iconRepository.getData(checkpoint.name)) {
            is Loading.Success -> iconResponse.data
            else -> null
        }
        return MarkerOptions()
                .position(LatLng(checkpoint.latitude, checkpoint.longitude))
                .title(checkpoint.name)
                .icon(icon)

    }
}
