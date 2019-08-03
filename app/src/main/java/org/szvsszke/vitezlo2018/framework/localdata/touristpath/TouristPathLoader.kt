package org.szvsszke.vitezlo2018.framework.localdata.touristpath

import android.content.res.AssetManager
import io.ticofab.androidgpxparser.parser.GPXParser
import org.szvsszke.vitezlo2018.data.repository.DataSource
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.entity.Track
import org.szvsszke.vitezlo2018.framework.localdata.track.GpxTrackMapper
import org.szvsszke.vitezlo2018.map.data.FilePath
import timber.log.Timber
import javax.inject.Inject

/**
 * Loads a list of tourist paths
 */
class TouristPathLoader @Inject constructor(private val assets: AssetManager,
                                            private val gpxParser: GPXParser,
                                            private val gpxTrackMapper: GpxTrackMapper): DataSource<List<Track>> {

    override fun getData(): Loading<List<Track>> {
        return try {
            val trackPaths = assets.list(FilePath.PATH_TO_TOURIST_PATHS)
            val paths = mutableListOf<Track>()
            trackPaths?.forEach {
                paths.add(gpxTrackMapper.mapToTrack(gpxParser.parse(assets.open(it))))
            }
            Loading.Success(paths)
        } catch (exception: Exception) {
            Timber.e(exception)
            Loading.Failure(exception)
        }

    }

}
