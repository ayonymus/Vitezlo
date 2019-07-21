package org.szvsszke.vitezlo2018.framework.localdata

/**
 *
 * Base interface for retrieving different data types stored locally
 *
 */
interface DataSource<T> {

    fun getData(): T?

}
