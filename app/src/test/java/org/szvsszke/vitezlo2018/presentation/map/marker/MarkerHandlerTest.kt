package org.szvsszke.vitezlo2018.presentation.map.marker

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test

internal class MarkerHandlerTest {

    private val option1 = mock<MarkerOptions> { }
    private val option2 = mock<MarkerOptions> { }

    private val marker1 = mock<Marker> { }
    private val marker2 = mock<Marker> { }
    private val map = mock<GoogleMap> {
        on { addMarker(option1) } doReturn marker1
        on { addMarker(option2) } doReturn marker2
    }

    private val markerHandler = MarkerHandler()

    @Test
    fun `given a google map and a list of MarkerOptions when addMarkers called then add markers to map`() {
        markerHandler.addMarkers(map, listOf(option1, option2))

        verify(map).addMarker(option1)
        verify(map).addMarker(option2)
    }

    @Test
    fun `given markers are added to the map when remove called then remove markers`() {
        markerHandler.addMarkers(map, listOf(option1, option2))

        markerHandler.removeMarkers()
        verify(marker1).remove()
        verify(marker2).remove()
    }
}
