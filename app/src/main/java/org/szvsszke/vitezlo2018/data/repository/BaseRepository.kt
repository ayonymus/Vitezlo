package org.szvsszke.vitezlo2018.data.repository

import org.szvsszke.vitezlo2018.domain.Repository

/**
 * Class to encapsulate common functionality of simple repositories
 */
open class BaseRepository<T>(private val source: DataSource<T>,
                             private val defaultValue: T): Repository<T> {

    private var cache: T = defaultValue

    override fun getData(): T {
        if(cache == defaultValue) {
            cache = source.getData()
        }
        return cache
    }
}
