package org.szvsszke.vitezlo2018.framework.localdata.sight

import android.content.res.AssetManager
import io.ticofab.androidgpxparser.parser.GPXParser
import org.szvsszke.vitezlo2018.data.repository.DataSource
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.entity.Sight
import timber.log.Timber
import javax.inject.Inject

class SightLoader @Inject constructor(private val assets: AssetManager,
                                      private val gpxParser: GPXParser,
                                      private val gpxSightMapper: GpxSightMapper
): DataSource<List<Sight>> {

    override fun getData() =
            try {
                Loading.Success(gpxSightMapper.mapToSightList(
                        gpxParser.parse(assets.open(PATH_SIGHTS_GPX))))
            } catch (exception: Exception) {
                Timber.e(exception)
                Loading.Failure<List<Sight>>()
            }

    companion object {
        const val PATH_SIGHTS_GPX = "Sights.gpx"
    }
}
