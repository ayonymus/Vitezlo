package org.szvsszke.vitezlo2018.presentation.map.marker

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.szvsszke.vitezlo2018.data.repository.ParameteredDataSource
import javax.inject.Inject

/**
 * Loads a Bitmap from the assets
 * TODO move to drawable?
 */
class TouristMarkIconSource  @Inject constructor(private val assets: AssetManager
): ParameteredDataSource<String, Bitmap> {

    override fun getData(key: String) = BitmapFactory.decodeStream(assets.open(PATH_MARKS + key + PNG))

    companion object {

        private const val PNG = ".png"
        private const val PATH_MARKS = "touristmarks/"

    }
}
