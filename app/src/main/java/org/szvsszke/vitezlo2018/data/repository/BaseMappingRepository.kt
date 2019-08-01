package org.szvsszke.vitezlo2018.data.repository

import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.MappingRepository

/**
 * Simple generic repository that is providing on demand data
 * retrieval and caching
 */
class BaseMappingRepository<K, T>(private val source: ParameteredDataSource<K, T>
): MappingRepository<K, T> {

    private val cache = mutableMapOf<K, T>()

    override fun getData(key: K): Loading<T> {
        var value = cache[key]
        if(value == null)
            value = source.getData(key)
            value?.apply {
                cache[key] = this
                return Loading.Success(value)
            }
        return Loading.Failure()
    }

}
