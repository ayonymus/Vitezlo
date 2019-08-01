package org.szvsszke.vitezlo2018.domain

/**
 * A repository that retrieves a little piece of data on demand, not a bigger data set at once
 */
interface MappingRepository<K, T> {

    fun getData(key: K): Loading<T>

}
