package org.szvsszke.vitezlo2018.data.repository

/**
 * Base interface for retrieving data
 */
interface DataSource<T> {

    fun getData(): T

}
