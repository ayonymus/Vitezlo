package org.szvsszke.vitezlo2018.presentation.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import org.szvsszke.vitezlo2018.domain.entity.Sight
import org.szvsszke.vitezlo2018.usecase.CheckpointState
import org.szvsszke.vitezlo2018.usecase.GetCheckpoints
import org.szvsszke.vitezlo2018.usecase.GetSights
import org.szvsszke.vitezlo2018.usecase.SightsState
import kotlin.test.assertEquals

internal class MapViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val id = "checkpoint"
    private val checkpoints = listOf(mock<Checkpoint> { })
    private val getCheckpoints = mock<GetCheckpoints> {
        on { invoke(arrayOf(id)) } doReturn CheckpointState.Data(checkpoints)
    }

    private val sight = mock<Sight> { }
    private val sights = listOf(sight)
    private val getSights = mock<GetSights> {
        on { invoke() } doReturn SightsState.Data(sights)
    }

    private lateinit var viewModel: MapViewModel

    @Before
    fun setUp() {
        viewModel = MapViewModel(getCheckpoints, getSights, Dispatchers.Unconfined)
    }

    @Test
    fun `given checkpoint data when observed then return checkpoint state`() {
        viewModel.getCheckpoints(arrayOf(id))
        assertEquals(CheckpointState.Data(checkpoints), viewModel.getCheckpoints(arrayOf(id)).value)
    }

    @Test
    fun `given sight data when observed then return sights state`() {
        viewModel.getSights()
        assertEquals(SightsState.Data(sights), viewModel.getSights().value)
    }
}
