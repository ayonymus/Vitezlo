package org.szvsszke.vitezlo2018.domain.entity

data class Checkpoint(
        val id: String,
        val name: String,
        val position: Int,
        val latitude: Double,
        val longitude: Double
)
