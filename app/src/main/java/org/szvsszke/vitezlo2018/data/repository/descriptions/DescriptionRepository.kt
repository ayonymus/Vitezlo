package org.szvsszke.vitezlo2018.data.repository.descriptions

import org.szvsszke.vitezlo2018.data.repository.BaseRepository
import org.szvsszke.vitezlo2018.data.repository.DataSource
import org.szvsszke.vitezlo2018.domain.entity.Description
import javax.inject.Inject

class DescriptionRepository @Inject constructor(source: DataSource<List<Description>>
): BaseRepository<List<Description>>(source)