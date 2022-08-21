package com.graphite.competitionplanner.category.domain

import com.graphite.competitionplanner.category.api.CustomCategorySpec
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestAddCustomCategory(
        @Autowired val findCompetitions: FindCompetitions,
        @Autowired val getCategories: GetCategories,
        @Autowired val addCustomCategory: AddCustomCategory
) {

    @Test
    fun shouldAddCustomCategory() {
        val competitions = findCompetitions.thatStartOrEndWithin(LocalDate.now().minusYears(2), LocalDate.now())
        val firstCompetitionId = competitions[0].id
        val originalSizeWithCompetitionId = getCategories.execute(firstCompetitionId)
        val originalSizeNoCompetitionId = getCategories.execute(0)

        addCustomCategory.execute(firstCompetitionId, CustomCategorySpec("Monkey", CategoryType.SINGLES))

        val newSizeWithCompetitionId = getCategories.execute(firstCompetitionId)
        val newSizeNoCompetitionId = getCategories.execute(0)

        Assertions.assertEquals(originalSizeWithCompetitionId.size + 1, newSizeWithCompetitionId.size)

        // Should not be fetchable for other competitions
        Assertions.assertEquals(originalSizeNoCompetitionId.size, newSizeNoCompetitionId.size)
    }

}