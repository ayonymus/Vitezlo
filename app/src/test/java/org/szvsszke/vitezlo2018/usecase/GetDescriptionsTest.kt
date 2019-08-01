package org.szvsszke.vitezlo2018.usecase

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.szvsszke.vitezlo2018.data.repository.descriptions.DescriptionRepository
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.entity.Description
import kotlin.test.assertEquals

internal class GetDescriptionsTest {

    private val description = mock<Description> { }
    private val descriptions = listOf(description)
    private val descriptionResult = Loading.Success(descriptions)

    private val repo = mock<DescriptionRepository> {
        on { getData() } doReturn descriptionResult
    }

    private val getSights = GetDescriptions(repo)

    @Test
    fun `given a list of ids when checkpoints called then return data`() {
        val result = getSights.invoke()
        verify(repo).getData()
        assertEquals(DescriptionsState.Data(descriptions), result)
    }

    @Test
    fun `given a list of ids when checkpoints call errors then return error`() {
        given { repo.getData() }.willReturn(Loading.Failure())

        val result = getSights.invoke()
        verify(repo).getData()
        assertEquals(DescriptionsState.Error, result)
    }
}
