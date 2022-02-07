package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.*
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.util.DataGenerator
import org.jooq.DSLContext
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestGetDraw(@Autowired val dslContext: DSLContext, @Autowired val createDraw: CreateDraw) {

    private val mockedCompetitionCategoryRepository = Mockito.mock(ICompetitionCategoryRepository::class.java)
    private val mockedMatchService = Mockito.mock(MatchService::class.java)
    private val mockedCalculateGroupStanding = Mockito.mock(CalculateGroupStanding::class.java)
    private val getDraw = GetDraw(mockedCompetitionCategoryRepository,
        mockedMatchService, dslContext, mockedCalculateGroupStanding)

    val dataGenerator = DataGenerator()

//    @Test
//    fun shouldReturnWhatRepositoryReturns() {
//        // Setup
//        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
//            id = 15236,
//            settings = dataGenerator.newGeneralSettingsSpec(
//                drawType = DrawType.POOL_AND_CUP,
//                playersPerGroup = 4,
//                playersToPlayOff = 2
//            )
//        )
//
//        val drawDto = CompetitionCategoryDrawDTO(
//            competitionCategory.id,
//            playOff = listOf(
//                PlayoffRoundDTO(Round.SEMI_FINAL, mutableListOf(newPlaceholderMatch(Round.SEMI_FINAL, 1))),
//                PlayoffRoundDTO(Round.FINAL, mutableListOf(newPlaceholderMatch(Round.SEMI_FINAL, 1))),
//            ),
//            groups = listOf(
//                newGroupDrawDtoWithPlayerNames("A", listOf("Steeve", "Charles", "Klark", "Kent")),
//                newGroupDrawDtoWithPlayerNames("B", listOf("Steeve2", "Charles2", "Klark2", "Kent2"))
//            ),
//            emptyList()
//        )
//
//        Mockito.`when`(mockedCompetitionCategoryRepository.get(competitionCategory.id)).thenReturn(competitionCategory)
//
//        // Act
//        createDraw.execute(competitionCategory.id)
//        val result = getDraw.execute(competitionCategory.id)
//
//        // Act
//        Assertions.assertEquals(drawDto, result)
//    }

    private fun newPlaceholderMatch(round: Round, order: Int): MatchAndResultDTO {
        return dataGenerator.newMatchAndResultDTO(
            firstPlayer = listOf(dataGenerator.newPlayerWithClubDTO(firstName = "Placeholder", lastName = "Placeholder")),
            secondPlayer = listOf(dataGenerator.newPlayerWithClubDTO(firstName = "Placeholder", lastName = "Placeholder")),
            groupOrRound = round.name,
            orderNumber = order
        )
    }

    private fun newGroupDrawDtoWithPlayerNames(groupName: String, names: List<String>): GroupDrawDTO {
        val playerList: MutableList<PlayerInPoolDTO> = mutableListOf()
        for (name in names) {
            playerList.add(PlayerInPoolDTO(mutableListOf(dataGenerator.newPlayerWithClubDTO(firstName = name)), 1))
        }
        return GroupDrawDTO(
            groupName,
            playerList,
            emptyList(),
            mutableListOf()
        )
    }
}