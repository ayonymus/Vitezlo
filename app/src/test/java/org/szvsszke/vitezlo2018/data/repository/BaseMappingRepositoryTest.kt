package org.szvsszke.vitezlo2018.data.repository

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.Loading
import kotlin.test.assertEquals

internal class BaseMappingRepositoryTest {

    private val key1 = "key1"
    private val val1 = "val1"

    private val source = mock<ParameteredDataSource<String, String>> { }

    private lateinit var repository: BaseMappingRepository<String, String>

    @Before
    fun setUp() {
        repository = BaseMappingRepository(source)
    }

    @Test
    fun `given data not in source when getData called then return failure`() {
        val result = repository.getData(key1)

        assertEquals(Loading.Failure(), result)
        verify(source).getData(key1)
    }


    @Test
    fun `given data not cached when getData called then return data from source`() {
        given { source.getData(key1) }.willReturn(val1)

        val result = repository.getData(key1)
        assertEquals(Loading.Success(val1), result)

        verify(source, times(1)).getData(key1)
    }

    @Test
    fun `given data already cached when getData called then return data from cache`() {
        given { source.getData(key1) }.willReturn(val1)

        repository.getData(key1)
        val result = repository.getData(key1)

        assertEquals(Loading.Success(val1), result)
        verify(source, times(1)).getData(key1)
    }
}
