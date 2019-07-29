package org.szvsszke.vitezlo2018.usecase

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.szvsszke.vitezlo2018.data.repository.sight.SightRepository
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.entity.Sight
import kotlin.test.assertEquals

internal class GetSightsTest {

    private val sight = mock<Sight> { }
    private val sights = listOf(sight)
    private val sightsResult = Loading.Success(sights)

    private val repo = mock<SightRepository> {
        on { getData() } doReturn sightsResult
    }

    private val getSights = GetSights(repo)

    @Test
    fun `given a list of ids when checkpoints called then return data`() {
        val result = getSights.invoke()
        verify(repo).getData()
        assertEquals(SightsState.Data(sights), result)
    }

    @Test
    fun `given a list of ids when checkpoints call errors then return error`() {
        given { repo.getData() }.willReturn(Loading.Failure())

        val result = getSights.invoke()
        verify(repo).getData()
        assertEquals(SightsState.Error, result)
    }
}
