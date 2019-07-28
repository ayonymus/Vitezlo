package org.szvsszke.vitezlo2018.data.repository

interface ParameteredDataSource<K, T> {

    fun getData(key: K): T?

}
