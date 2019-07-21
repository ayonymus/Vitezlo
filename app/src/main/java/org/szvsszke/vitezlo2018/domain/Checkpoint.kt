package org.szvsszke.vitezlo2018.domain

data class Checkpoint(
        val id: String,
        val name: String,
        val position: Int,
        val latitude: Double,
        val longitude: Double
)
