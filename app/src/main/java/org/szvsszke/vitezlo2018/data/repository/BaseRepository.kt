package org.szvsszke.vitezlo2018.data.repository

import org.szvsszke.vitezlo2018.domain.Repository
import org.szvsszke.vitezlo2018.framework.localdata.DataSource

/**
 * Class to encapsulate common functionality of simple repositories
 */
abstract class BaseRepository<T>(protected val source: DataSource<T>,
                                 protected val defaultValue: T): Repository<T> {

    private var cache: T? = null

    override fun getData(): T {
        if(cache == null) {
            cache = source.getData()
        }
        return cache?.let {
            it
        } ?: defaultValue
    }

}
