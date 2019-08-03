package org.szvsszke.vitezlo2018.domain.entity

data class Track(val trackName: String,
                 val points: List<Point>,
                 val description: String? = null)