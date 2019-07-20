package org.szvsszke.vitezlo2018.data.repository

import org.szvsszke.vitezlo2018.domain.Checkpoint
import org.szvsszke.vitezlo2018.framework.localdata.checkpoint.CheckpointLoader

/**
 * Repository for handling the checkpoint data
 */
class CheckpointRepository(checkpointLoader: CheckpointLoader
): BaseRepository<List<Checkpoint>>(checkpointLoader, emptyList())
