package org.szvsszke.vitezlo2018.data.repository.sight

import java.lang.Exception

interface LoadingResult<T> {

    class Empty<T> : LoadingResult<T>
    data class Data<T>(val data: T) : LoadingResult<T>
    data class Error<T>(val exception: Exception? = null) : LoadingResult<T>

}


