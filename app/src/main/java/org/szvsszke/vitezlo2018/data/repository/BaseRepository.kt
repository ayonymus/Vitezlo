package org.szvsszke.vitezlo2018.data.repository

import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.Repository

/**
 * Class to encapsulate common functionality of simple repositories
 */
open class BaseRepository<T>(private val source: DataSource<T>): Repository<T> {

    private var cache: Loading<T> = Loading.NotStarted()

    override fun getData(): Loading<T> {
        if(cache is Loading.NotStarted || cache is Loading.Failure) {
            cache = source.getData()
        }
        return cache
    }
}
