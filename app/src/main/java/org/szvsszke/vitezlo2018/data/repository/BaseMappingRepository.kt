package org.szvsszke.vitezlo2018.data.repository

import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.MappingRepository
import java.lang.Exception

/**
 * Simple generic repository that is providing on demand data
 * retrieval and caching
 */
open class BaseMappingRepository<K, T>(private val source: ParameteredDataSource<K, T>
): MappingRepository<K, T> {

    private val cache = mutableMapOf<K, T>()

    override fun getData(key: K): Loading<T> {
        var value = cache[key]
        return try {
            if (value == null)
                value = source.getData(key)
            value?.apply {
                cache[key] = this
                return Loading.Success(value)
            }
            Loading.Failure()
        }catch (exception: Exception) {
            Loading.Failure(exception)
        }
    }

}
