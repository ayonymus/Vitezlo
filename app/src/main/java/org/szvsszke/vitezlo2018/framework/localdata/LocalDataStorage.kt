package org.szvsszke.vitezlo2018.framework.localdata

/**
 * Base interface for retrieving different data types stored locally
 *
 * todo is this a domain interface?
 */
interface LocalDataStorage<T> {

    fun load(): T?

}
