package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.entity.Point

/**
 * Given a list of [Point]s find a the top left and bottom right
 * position of the area where all of them are enclosed.
 */
class FindBounds {

    operator fun invoke(points: List<Point>): Pair<Point, Point> {
        if (points.isEmpty()) {
            throw IllegalArgumentException()
        }

        var latLow = points[0].latitude
        var latHi = points[0].latitude
        var lonLow = points[0].longitude
        var lonHi = points[0].longitude

        for (coordinate in points) {
            if (coordinate.latitude < latLow) {
                latLow = coordinate.latitude
            } else if (coordinate.latitude > latHi) {
                latHi = coordinate.latitude
            }

            if (coordinate.longitude < lonLow) {
                lonLow = coordinate.longitude
            } else if (coordinate.longitude > lonHi) {
                lonHi = coordinate.longitude
            }
        }
        return Pair(Point(latLow, lonLow), Point(latHi, lonHi))
    }
}
