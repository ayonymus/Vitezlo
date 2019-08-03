package org.szvsszke.vitezlo2018.presentation.map.marker

import android.graphics.Bitmap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.MappingRepository
import org.szvsszke.vitezlo2018.domain.entity.Point
import javax.inject.Inject


class TouristMarkFactory @Inject constructor(private val iconRepository: MappingRepository<String, Bitmap>) {

    fun create(marker: String, checkpoint: Point): MarkerOptions {
        val icon = when (val iconResponse = iconRepository.getData(marker)) {
            is Loading.Success -> createScaledIcon(iconResponse.data)
            else -> null
        }

        return MarkerOptions()
                .position(LatLng(checkpoint.latitude, checkpoint.longitude))
                .title(TOURIST_MARK)
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    private fun createScaledIcon(icon: Bitmap) = Bitmap.createScaledBitmap(icon, SCALE, SCALE, true)

    companion object {
        // TODO should not be a const
        private const val SCALE = 30

        private const val TOURIST_MARK = "touristmark"

    }

}
