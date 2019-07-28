package org.szvsszke.vitezlo2018.domain

import java.lang.Exception

interface Result<T> {

    class Empty<T> : Result<T>
    data class Data<T>(val data: T) : Result<T>
    data class Error<T>(val exception: Exception? = null) : Result<T>

}
