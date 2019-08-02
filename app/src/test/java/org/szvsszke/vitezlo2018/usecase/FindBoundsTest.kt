package org.szvsszke.vitezlo2018.usecase

import org.junit.Test
import org.szvsszke.vitezlo2018.domain.entity.Point
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class FindBoundsTest {

    private val point1 = Point(1.0, 1.0)
    private val point2 = Point(2.0, -1.0)
    private val point3 = Point(-3.0, 4.0)

    private val findBounds = FindBounds()

    @Test
    fun `given a list of points then return a pair of bounding points`() {
        val expected = Pair(Point(-3.0, -1.0), Point(2.0, 4.0))
        val bounds = findBounds(listOf(point1, point2, point3))
        assertEquals(expected, bounds)
    }

    @Test
    fun `given an empty list of points then throw value error`() {
        assertFailsWith<IllegalArgumentException> { findBounds(emptyList()) }
    }
}
