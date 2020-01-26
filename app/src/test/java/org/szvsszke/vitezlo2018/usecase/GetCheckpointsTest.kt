package org.szvsszke.vitezlo2018.usecase

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Test
import org.szvsszke.vitezlo2018.data.repository.checkpoint.CheckpointRepository
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.preferences.UserPreferences
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import kotlin.test.assertEquals

internal class GetCheckpointsTest {

    private val checkpoints = mapOf<String, Checkpoint>()
    private val checkpointsResult = Loading.Success(checkpoints)
    private val ids = listOf("")
    private val checkpoint = mock<Checkpoint> { }
    private val checkpointList = listOf(checkpoint)

    private val repo = mock<CheckpointRepository> {
        on { getData() } doReturn checkpointsResult
    }
    private val mapper = mock<GetCheckpointListFromMap> {
        on { invoke(checkpoints, ids) } doReturn checkpointList
    }
    private val preferences = mock<UserPreferences> {
        on { areCheckPointsEnabled() } doReturn true
    }

    private val getCheckpoints = GetCheckpoints(repo, mapper, preferences)

    @Test
    fun `given a list of ids when checkpoints called then return data`() {
        val result = getCheckpoints.invoke(ids)
        verify(repo).getData()
        assertEquals(CheckpointState.Data(checkpointList), result)
    }

    @Test
    fun `given a list of ids when checkpoints call errors then return error`() {
        given { repo.getData() }.willReturn(Loading.Failure())

        val result = getCheckpoints.invoke(ids)
        verify(repo).getData()
        assertEquals(CheckpointState.Error, result)

    }

    @Test
    fun `given a list of ids when checkpoints are disabled then return disabled`() {
        given { preferences.areCheckPointsEnabled() }.willReturn(false)

        val result = getCheckpoints.invoke(ids)
        verifyNoMoreInteractions(repo)
        assertEquals(CheckpointState.Disabled, result)

    }

}
