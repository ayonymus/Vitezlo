package org.szvsszke.vitezlo2018.presentation.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import org.szvsszke.vitezlo2018.domain.entity.Description
import org.szvsszke.vitezlo2018.domain.entity.Sight
import org.szvsszke.vitezlo2018.domain.entity.Track
import org.szvsszke.vitezlo2018.usecase.CheckpointState
import org.szvsszke.vitezlo2018.usecase.DescriptionsState
import org.szvsszke.vitezlo2018.usecase.GetCheckpoints
import org.szvsszke.vitezlo2018.usecase.GetDescriptions
import org.szvsszke.vitezlo2018.usecase.GetSights
import org.szvsszke.vitezlo2018.usecase.GetTrack
import org.szvsszke.vitezlo2018.usecase.SightsState
import org.szvsszke.vitezlo2018.usecase.TrackState
import kotlin.test.assertEquals

internal class MapViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val id = "checkpoint"
    private val checkpoints = listOf(mock<Checkpoint> { })
    private val getCheckpoints = mock<GetCheckpoints> {
        on { invoke(listOf(id)) } doReturn CheckpointState.Data(checkpoints)
    }

    private val sight = mock<Sight> { }
    private val sights = listOf(sight)
    private val getSights = mock<GetSights> {
        on { invoke() } doReturn SightsState.Data(sights)
    }

    private val description = mock<Description> { }
    private val descriptions = listOf(description)
    private val getDescriptions = mock<GetDescriptions> {
        on { invoke() } doReturn DescriptionsState.Data(descriptions)
    }

    private val track = mock<Track> { }
    private val trackId = "some track"
    private val getTrack = mock<GetTrack> {
        on { invoke(trackId) } doReturn TrackState.Data(track)
    }

    private lateinit var viewModel: MapViewModel

    @Before
    fun setUp() {
        viewModel = MapViewModel(getCheckpoints, getSights, getDescriptions, getTrack, Dispatchers.Unconfined)
    }

    @Test
    fun `given checkpoint data when observed then return checkpoint state`() {
        viewModel.getCheckpoints(listOf(id))
        assertEquals(CheckpointState.Data(checkpoints), viewModel.getCheckpoints(listOf(id)).value)
    }

    @Test
    fun `given sight data when observed then return sights state`() {
        viewModel.getSights()
        assertEquals(SightsState.Data(sights), viewModel.getSights().value)
    }

    @Test
    fun `given description data when observed then return description state`() {
        viewModel.getDescriptions()
        assertEquals(DescriptionsState.Data(descriptions), viewModel.getDescriptions().value)
    }

    @Test
    fun `given track data when observed then return track state`() {
        viewModel.getTrack(trackId)
        assertEquals(TrackState.Data(track), viewModel.getTrack(trackId).value)
    }

}
