package org.szvsszke.vitezlo2018.framework.localdata.checkpoint

import android.content.res.AssetManager
import io.ticofab.androidgpxparser.parser.GPXParser
import org.szvsszke.vitezlo2018.data.repository.DataSource
import org.szvsszke.vitezlo2018.domain.Result
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import org.szvsszke.vitezlo2018.map.data.FilePath
import timber.log.Timber
import javax.inject.Inject

/**
 * Loader class to get checkpoint data from a local gpx asset file
 */
class CheckpointLoader @Inject constructor(private val assets: AssetManager,
                                           private val gpxParser: GPXParser,
                                           private val gpxCheckpointMapper: GpxCheckpointMapper
): DataSource<Map<String, Checkpoint>> {

    override fun getData(): Result<Map<String, Checkpoint>> {
        return try {
            Result.Data(gpxCheckpointMapper.mapToCheckPointMap(
                    gpxParser.parse(
                            assets.open(FilePath.FILE_CHECKPOINTS_GPX))))
        } catch (exception: Exception) {
            Timber.e(exception)
            Result.Error()
        }

    }

}
