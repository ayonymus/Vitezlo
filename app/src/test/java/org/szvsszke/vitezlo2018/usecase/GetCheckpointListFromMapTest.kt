package org.szvsszke.vitezlo2018.usecase

import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import kotlin.test.assertEquals

class GetCheckpointListFromMapTest {

    private val id1 = "id1"
    private val id2 = "id2"
    private val id3 = "id3"

    private val checkpoint1 = mock<Checkpoint> {}
    private val checkpoint2 = mock<Checkpoint> {}
    private val checkpoint3 = mock<Checkpoint> {}


    private val mapper = GetCheckpointListFromMap()

    @Test
    fun `given map containing all necessary checkpoints then return needed checkpoints`() {
        val map = mapOf(Pair(id1, checkpoint1),
                Pair(id2, checkpoint2),
                Pair(id3, checkpoint3))

        val result = mapper.invoke(map, arrayOf(id1, id3))

        assertEquals(listOf(checkpoint1, checkpoint3), result)
    }

    @Test
    fun `given map does not contain all necessary checkpoints then return available checkpoints`() {
        val map = mapOf(Pair(id1, checkpoint1),
                Pair(id2, checkpoint2))

        val result = mapper.invoke(map, arrayOf(id1, id3))

        assertEquals(listOf(checkpoint1), result)
    }

    @Test
    fun `given map is empty then return empty list`() {
        val map = mapOf<String, Checkpoint>()

        val result = mapper.invoke(map, arrayOf(id1, id3))

        assertEquals(listOf(), result)
    }

    @Test
    fun `given map not empty when id list is empty then return empty list`() {
        val map = mapOf(Pair(id1, checkpoint1),
                Pair(id2, checkpoint2))
        val result = mapper.invoke(map, arrayOf())

        assertEquals(listOf(), result)
    }


}
