package org.szvsszke.vitezlo2018.data.repository.sight

 import org.szvsszke.vitezlo2018.data.repository.BaseRepository
import org.szvsszke.vitezlo2018.domain.entity.Sight
import org.szvsszke.vitezlo2018.framework.localdata.sight.SightLoader
import javax.inject.Inject

class SightRepository @Inject constructor(sightLoader: SightLoader
): BaseRepository<List<Sight>>(sightLoader)