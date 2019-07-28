package org.szvsszke.vitezlo2018.usecase

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.szvsszke.vitezlo2018.data.CheckpointListMapper
import org.szvsszke.vitezlo2018.data.repository.checkpoint.CheckpointRepository
import org.szvsszke.vitezlo2018.data.repository.checkpoint.CheckpointResult
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import kotlin.test.assertEquals

internal class GetCheckpointsTest {

    private val checkpoints = mapOf<String, Checkpoint>()
    private val checkpointsResult = CheckpointResult.Data(checkpoints)
    private val ids = arrayOf("")
    private val checkpoint = mock<Checkpoint> { }
    private val checkpointList = listOf(checkpoint)

    private val repo = mock<CheckpointRepository> {
        on { getData() } doReturn checkpointsResult
    }
    private val mapper = mock<CheckpointListMapper> {
        on { mapCheckpoint(checkpoints, ids) } doReturn checkpointList
    }

    private val getCheckpoints = GetCheckpoints(repo, mapper)

    @Test
    fun `given a list of ids when checkpoints called then return data`() {
        val result = getCheckpoints.invoke(ids)
        verify(repo).getData()
        assertEquals(CheckpointState.Data(checkpointList), result)
    }

    @Test
    fun `given a list of ids when checkpoints call errors then return error`() {
        given { repo.getData() }.willReturn(CheckpointResult.Error)

        val result = getCheckpoints.invoke(ids)
        verify(repo).getData()
        assertEquals(CheckpointState.Error, result)

    }
}
