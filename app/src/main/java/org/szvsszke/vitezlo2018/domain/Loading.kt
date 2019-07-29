package org.szvsszke.vitezlo2018.domain

interface Loading<T> {

    class NotStarted<T> : Loading<T>
    data class Success<T>(val data: T) : Loading<T>
    data class Failure<T>(val exception: Throwable? = null) : Loading<T>

}
