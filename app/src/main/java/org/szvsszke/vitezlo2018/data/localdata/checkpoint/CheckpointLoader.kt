package org.szvsszke.vitezlo2018.data.localdata.checkpoint

import android.content.res.AssetManager
import io.ticofab.androidgpxparser.parser.GPXParser
import org.szvsszke.vitezlo2018.data.repository.DataSource
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Loader class to get checkpoint data from a local gpx asset file
 */
class CheckpointLoader @Inject constructor(private val assets: AssetManager,
                                           private val gpxParser: GPXParser,
                                           private val gpxCheckpointMapper: GpxCheckpointMapper
): DataSource<Map<String, Checkpoint>> {

    override fun getData(): Loading<Map<String, Checkpoint>> {
        return try {
            Loading.Success(gpxCheckpointMapper.mapToCheckPointMap(
                    gpxParser.parse(
                            assets.open(PATH_CHECKPOINTS_GPX))))
        } catch (exception: Exception) {
            Timber.e(exception)
            Loading.Failure(exception)
        }

    }

    companion object {
        const val PATH_CHECKPOINTS_GPX = "CheckPoints.gpx"
    }

}
