package org.szvsszke.vitezlo2018.usecase

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.entity.Point
import org.szvsszke.vitezlo2018.domain.preferences.MapPreferences
import org.szvsszke.vitezlo2018.domain.preferences.MapStatus
import kotlin.test.assertEquals

class GetMapStatusTest {

    private val mapStatus = MapStatus(3, Point(12.0, 12.0), 1F)
    private val mapPreferences = mock<MapPreferences> {
        on { getMapStatus() } doReturn mapStatus
    }

    @Test
    fun `when invoked then return MapStatus from preferences`() {
        val getMapStatus = GetMapStatus(mapPreferences)
        val result = getMapStatus()
        assertEquals(mapStatus, result)
    }
}