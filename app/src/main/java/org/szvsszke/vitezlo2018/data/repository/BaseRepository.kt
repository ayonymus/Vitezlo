package org.szvsszke.vitezlo2018.data.repository

import org.szvsszke.vitezlo2018.domain.Repository

/**
 * Class to encapsulate common functionality of simple repositories
 */
class BaseRepository<T>(private val source: DataSource<T>,
                        private val defaultValue: T): Repository<T> {

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
