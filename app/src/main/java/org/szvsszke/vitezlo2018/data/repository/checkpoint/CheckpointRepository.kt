package org.szvsszke.vitezlo2018.data.repository.checkpoint

import org.szvsszke.vitezlo2018.data.repository.BaseRepository
import org.szvsszke.vitezlo2018.data.repository.DataSource
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import javax.inject.Inject

/**
 * Repository for handling the checkpoint data
 */
class CheckpointRepository @Inject constructor(checkpointLoader: DataSource<Map<String, Checkpoint>>
): BaseRepository<Map<String, Checkpoint>>(checkpointLoader)
