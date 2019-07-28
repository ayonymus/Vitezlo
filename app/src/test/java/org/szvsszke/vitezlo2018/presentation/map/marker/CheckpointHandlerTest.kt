package org.szvsszke.vitezlo2018.presentation.map.marker

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint

internal class CheckpointHandlerTest {

    private val checkpoint = mock<Checkpoint> { }
    private val marker = mock<MarkerOptions> { }

    private val map = mock<GoogleMap> { }

    private val markerFactory = mock<CheckpointMarkerFactory> {
        on { create(checkpoint) } doReturn marker
    }

    private val markerHandler = mock<MarkerHandler> { }

    private val checkpointHandler = CheckpointHandler(markerFactory, markerHandler)

    @Test
    fun `given a list of checkpoints then create a list of markers and display on map`() {
        checkpointHandler.showCheckpoints(map, listOf(checkpoint))
        verify(markerHandler).removeMarkers()
        verify(markerHandler).addMarkers(map, listOf(marker))
    }

    @Test
    fun `when hide checkpoints then forward to markerHandler`() {
        checkpointHandler.hideCheckpoints()
        verify(markerHandler).removeMarkers()
    }
}
