package org.szvsszke.vitezlo2018.framework.localdata.track

import android.content.res.AssetManager
import io.ticofab.androidgpxparser.parser.GPXParser
import org.szvsszke.vitezlo2018.data.repository.DataSource
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.entity.Track
import org.szvsszke.vitezlo2018.map.data.FilePath
import timber.log.Timber
import javax.inject.Inject

class TrackLoader @Inject constructor(private val assets: AssetManager,
                                      private val gpxParser: GPXParser,
                                      private val gpxTrackMapper: GpxTrackMapper
): DataSource<Track> {

    override fun getData() =
            try {
                Loading.Success(gpxTrackMapper.mapToTrack(
                        gpxParser.parse(assets.open(FilePath.FILE_SIGHTS_GPX))))
            } catch (exception: Exception) {
                Timber.e(exception)
                Loading.Failure<Track>()
            }
}
