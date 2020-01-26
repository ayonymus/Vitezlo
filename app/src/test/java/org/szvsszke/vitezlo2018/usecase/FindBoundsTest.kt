package org.szvsszke.vitezlo2018.usecase

import org.junit.Test
import org.szvsszke.vitezlo2018.domain.entity.Point
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class FindBoundsTest {

    private val point1 = Point(1.0, 1.0)
    private val point2 = Point(2.0, -1.0)
    private val point3 = Point(-3.0, 4.0)

    private val point4 = Point(4.0, -2.0)
    private val point5 = Point(-7.0, 3.0)

    private val findBounds = FindBounds()

    @Test
    fun `given a list of points then return a pair of bounding points`() {
        val expected = Pair(Point(-3.0, -1.0), Point(2.0, 4.0))
        val boundsOf = findBounds(listOf(point1, point2, point3))
        assertEquals(expected, boundsOf)
    }

    @Test
    fun `given a bigger list of points then return a pair of bounding points`() {
        val expected = Pair(Point(-7.0, -2.0), Point(4.0, 4.0))
        val boundsOf = findBounds(listOf(point1, point2, point3, point4, point5))
        assertEquals(expected, boundsOf)
    }

    @Test
    fun `given an empty list of points then throw value error`() {
        assertFailsWith<IllegalArgumentException> { findBounds(emptyList()) }
    }
}
