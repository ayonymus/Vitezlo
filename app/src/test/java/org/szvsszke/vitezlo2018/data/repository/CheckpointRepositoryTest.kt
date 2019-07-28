package org.szvsszke.vitezlo2018.data.repository

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.szvsszke.vitezlo2018.data.repository.checkpoint.CheckpointRepository
import org.szvsszke.vitezlo2018.data.repository.checkpoint.CheckpointResult
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import org.szvsszke.vitezlo2018.framework.localdata.checkpoint.CheckpointLoader
import kotlin.test.assertEquals

class CheckpointRepositoryTest {

    private val checkpoint1 = mock<Checkpoint> { }
    private val checkpoint2 = mock<Checkpoint> { }
    private val checkpoints = mapOf(Pair("A", checkpoint1), Pair("B", checkpoint2))

    private val checkpointLoader = mock<CheckpointLoader> {
        on { getData() } doReturn CheckpointResult.Data(checkpoints)
    }

    private lateinit var repository: CheckpointRepository

    @Before
    fun setUp() {
        repository = CheckpointRepository(checkpointLoader)
    }

    @Test
    fun `given no cached data when data requested then should load from disk`() {
        val result = repository.getData()

        assertEquals(CheckpointResult.Data(checkpoints), result)
        verify(checkpointLoader, times(1)).getData()
    }

    @Test
    fun `given cached data when data requested then should not load from disk again`() {
        repository.getData()
        val result = repository.getData()

        assertEquals(CheckpointResult.Data(checkpoints), result)
        verify(checkpointLoader, times(1)).getData()
    }

    @Test
    fun `given data is empty then should return empty map`() {
        given { checkpointLoader.getData() }.willReturn(CheckpointResult.Data(emptyMap()))

        val result = repository.getData()

        assertEquals(CheckpointResult.Data(emptyMap()), result)
        verify(checkpointLoader, times(1)).getData()
    }

    @Test
    fun `given loader returns error then propagate error`() {
        given { checkpointLoader.getData() }.willReturn(CheckpointResult.Error)

        val result = repository.getData()

        assertEquals(CheckpointResult.Error, result)
        verify(checkpointLoader, times(1)).getData()
    }
}
