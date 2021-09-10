package com.graphite.competitionplanner.domain.usecase.player

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.TestHelper
import com.graphite.competitionplanner.player.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.player.domain.ListAllPlayersInClub
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestListAllPlayersInClub {

    private final val mockedPlayerRepository = mock(IPlayerRepository::class.java)
    private final val mockedFindClub = mock(FindClub::class.java)
    private val listAllPlayersInClub = ListAllPlayersInClub(mockedPlayerRepository, mockedFindClub)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldAssertThatClubExist() {
        listAllPlayersInClub.execute(10)
        verify(mockedFindClub, atLeastOnce()).byId(10)
    }

    @Test
    fun shouldNotFetchPlayersIfClubNotFound() {
        `when`(mockedFindClub.byId(10)).thenThrow(NotFoundException::class.java)

        Assertions.assertThrows(NotFoundException::class.java) { listAllPlayersInClub.execute(10) }

        verify(mockedPlayerRepository, never()).playersInClub(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldCallRepositoryWhenClubIsFound() {
        val club = dataGenerator.newClubDTO()
        `when`(mockedFindClub.byId(club.id)).thenReturn(club)

        listAllPlayersInClub.execute(club.id)

        verify(mockedPlayerRepository, times(1)).playersInClub(club)
        verify(mockedPlayerRepository, times(1)).playersInClub(TestHelper.MockitoHelper.anyObject())
    }
}