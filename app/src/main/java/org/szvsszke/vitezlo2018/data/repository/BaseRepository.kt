package org.szvsszke.vitezlo2018.data.repository

import org.szvsszke.vitezlo2018.framework.localdata.LocalDataStorage

/**
 * Class to encapsulate common functionality of simple repositories
 */
abstract class BaseRepository<T>(protected val storage: LocalDataStorage<T>,
                                 protected val defaultValue: T) {

    private var cache: T? = null

    fun getData(): T {
        if(cache == null) {
            cache = storage.load()
        }
        return cache?.let {
            it
        } ?: defaultValue
    }

}
