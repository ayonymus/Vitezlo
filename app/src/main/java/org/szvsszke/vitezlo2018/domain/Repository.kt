package org.szvsszke.vitezlo2018.domain

/**
 * A simple repository that does not need any external parameters
 * to retrieve data
 */
interface Repository<T> {

    fun getData(): Loading<T>

}
