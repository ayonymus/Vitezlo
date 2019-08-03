package org.szvsszke.vitezlo2018.data.repository.sight

 import org.szvsszke.vitezlo2018.data.repository.BaseRepository
 import org.szvsszke.vitezlo2018.data.repository.DataSource
 import org.szvsszke.vitezlo2018.domain.entity.Sight
 import javax.inject.Inject

class SightRepository @Inject constructor(sightLoader: DataSource<List<Sight>>
): BaseRepository<List<Sight>>(sightLoader)
