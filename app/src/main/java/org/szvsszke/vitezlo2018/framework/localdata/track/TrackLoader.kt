package org.szvsszke.vitezlo2018.framework.localdata.track

import android.content.res.AssetManager
import io.ticofab.androidgpxparser.parser.GPXParser
import org.szvsszke.vitezlo2018.data.repository.ParameteredDataSource
import org.szvsszke.vitezlo2018.domain.entity.Track
import timber.log.Timber
import javax.inject.Inject

class TrackLoader @Inject constructor(private val assets: AssetManager,
                                      private val gpxParser: GPXParser,
                                      private val gpxTrackMapper: GpxTrackMapper
): ParameteredDataSource<String, Track> {

    override fun getData(key: String) =
            try {
                gpxTrackMapper.mapToTrack(
                        gpxParser.parse(assets.open(PATH_TO_ROUTES + key)))
            } catch (exception: Exception) {
                Timber.e(exception)
                null
            }

    companion object {
        const val PATH_TO_ROUTES = "routes/"
    }
}
