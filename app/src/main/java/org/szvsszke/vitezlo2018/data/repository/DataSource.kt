package org.szvsszke.vitezlo2018.data.repository

import org.szvsszke.vitezlo2018.domain.Result

/**
 * Base interface for retrieving data
 */
interface DataSource<T> {

    fun getData(): Result<T>

}
