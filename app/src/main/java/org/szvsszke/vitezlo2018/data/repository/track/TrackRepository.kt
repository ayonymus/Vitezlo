package org.szvsszke.vitezlo2018.data.repository.track

import org.szvsszke.vitezlo2018.data.repository.BaseMappingRepository
import org.szvsszke.vitezlo2018.data.repository.ParameteredDataSource
import org.szvsszke.vitezlo2018.domain.entity.Track
import javax.inject.Inject

class TrackRepository @Inject constructor(source: ParameteredDataSource<String, Track>
): BaseMappingRepository<String, Track>(source)
