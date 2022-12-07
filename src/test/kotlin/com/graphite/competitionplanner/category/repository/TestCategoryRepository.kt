package com.graphite.competitionplanner.category.repository

import com.graphite.competitionplanner.category.domain.CategoryType
import com.graphite.competitionplanner.category.domain.DefaultCategory
import com.graphite.competitionplanner.category.interfaces.CustomCategorySpec
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.util.BaseRepositoryTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.random.Random

@SpringBootTest
class TestCategoryRepository(
    @Autowired clubRepository: IClubRepository,
    @Autowired competitionRepository: ICompetitionRepository,
    @Autowired competitionCategoryRepository: ICompetitionCategoryRepository,
    @Autowired categoryRepository: ICategoryRepository,
    @Autowired playerRepository: IPlayerRepository,
    @Autowired registrationRepository: IRegistrationRepository,
    @Autowired matchRepository: MatchRepository,
    @Autowired resultRepository: IResultRepository,
) : BaseRepositoryTest(
    clubRepository,
    competitionRepository,
    competitionCategoryRepository,
    categoryRepository,
    playerRepository,
    registrationRepository,
    matchRepository,
    resultRepository
) {

    @Test
    fun whenCompetitionIdIsZeroDefaultCategoriesAreReturned() {
        // Setup
        val defaultCategoryName = DefaultCategory.values().first().name + Random.nextLong().toString()
        categoryRepository.addCategory(defaultCategoryName, CategoryType.SINGLES)

        // Act
        val categories = categoryRepository.getAvailableCategories()

        // Assert
        Assertions.assertTrue(categories.any { it.name == defaultCategoryName },
            "Did not find the default category with name $defaultCategoryName")
    }

    @Test
    fun whenCompetitionIdIsSpecifiedDefaultAndCustomCategoriesAreReturned() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()

        val defaultCategoryName = DefaultCategory.values().first().name + Random.nextLong().toString()
        categoryRepository.addCategory(defaultCategoryName, CategoryType.SINGLES)

        val customCategoryName = DefaultCategory.values().first().name + Random.nextLong().toString()
        categoryRepository.addCustomCategory(competition.id, CustomCategorySpec(customCategoryName, CategoryType.SINGLES))

        // Act
        val categories = categoryRepository.getAvailableCategories(competition.id)

        // Assert
        Assertions.assertTrue(categories.any { it.name == defaultCategoryName },
            "Did not find the default category with name $defaultCategoryName")
        Assertions.assertTrue(categories.filter { it.name == customCategoryName }.size == 1,
            "Did not find the custom category with name $customCategoryName")
    }
}