package org.szvsszke.vitezlo2018.data.repository

import org.szvsszke.vitezlo2018.domain.Result
import org.szvsszke.vitezlo2018.domain.Repository

/**
 * Class to encapsulate common functionality of simple repositories
 */
open class BaseRepository<T>(private val source: DataSource<T>): Repository<T> {

    private var cache: Result<T> = Result.Empty()

    override fun getData(): Result<T> {
        if(cache is Result.Empty || cache is Result.Error) {
            cache = source.getData()
        }
        return cache
    }
}
