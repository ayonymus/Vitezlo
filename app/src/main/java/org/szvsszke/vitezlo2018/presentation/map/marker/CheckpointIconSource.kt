package org.szvsszke.vitezlo2018.presentation.map.marker

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.ui.IconGenerator
import com.google.maps.android.ui.IconGenerator.STYLE_WHITE
import org.szvsszke.vitezlo2018.data.repository.ParameteredDataSource
import javax.inject.Inject

class CheckpointIconSource @Inject constructor(private val iconGenerator: IconGenerator
): ParameteredDataSource<String, BitmapDescriptor> {

    override fun getData(key: String): BitmapDescriptor {
        iconGenerator.apply {
            setStyle(STYLE_WHITE)
            setContentRotation(ROTATION)
        }
        return BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(key))
    }

    companion object {
        private const val ROTATION = 90
    }
}
