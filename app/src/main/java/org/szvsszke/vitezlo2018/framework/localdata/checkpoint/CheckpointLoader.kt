package org.szvsszke.vitezlo2018.framework.localdata.checkpoint

import android.content.res.AssetManager
import io.ticofab.androidgpxparser.parser.GPXParser
import org.szvsszke.vitezlo2018.domain.Checkpoint
import org.szvsszke.vitezlo2018.framework.localdata.LocalDataStorage
import org.szvsszke.vitezlo2018.map.data.FilePath
import timber.log.Timber

/**
 * Loader class to get checkpoint data from a local gpx asset file
 */
class CheckpointLoader(private val assets: AssetManager,
                       private val gpxParser: GPXParser,
                       private val gpxCheckpointMapper: GpxCheckpointMapper
): LocalDataStorage<Map<String, Checkpoint>> {

    override fun load(): Map<String, Checkpoint>? {
        // TODO error handling
        try {
            return gpxCheckpointMapper.mapToCheckPointMap(
                    gpxParser.parse(
                            assets.open(FilePath.FILE_CHECKPOINTS_GPX)))
        } catch (exception: Exception) {
            Timber.e(exception)
        }

        return emptyMap()
    }

}
