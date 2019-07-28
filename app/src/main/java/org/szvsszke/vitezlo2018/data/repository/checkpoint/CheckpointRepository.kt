package org.szvsszke.vitezlo2018.data.repository.checkpoint

import org.szvsszke.vitezlo2018.data.repository.BaseRepository
import org.szvsszke.vitezlo2018.framework.localdata.checkpoint.CheckpointLoader
import javax.inject.Inject

/**
 * Repository for handling the checkpoint data
 */
class CheckpointRepository @Inject constructor(checkpointLoader: CheckpointLoader
): BaseRepository<CheckpointResult>(checkpointLoader, CheckpointResult.Empty)
