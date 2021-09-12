package com.graphite.competitionplanner.competitioncategory.api

import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.domain.CreateClub
import com.graphite.competitionplanner.club.domain.DeleteClub
import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.util.AbstractApiTest
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompetitionCategory(
    @LocalServerPort port: Int,
    @Autowired testRestTemplate: TestRestTemplate,
    @Autowired val repository: ICompetitionCategoryRepository,
    @Autowired val addClub: CreateClub,
    @Autowired val deleteClub: DeleteClub,
    @Autowired val addCompetition: CreateCompetition,
    @Autowired val competitionRepository: CompetitionRepository,
    @Autowired val categoryRepository: ICategoryRepository
) : AbstractApiTest(
    port,
    testRestTemplate
) {
    override val resource: String = "/competition"
    val dataGenerator = DataGenerator()

    @Test
    fun canCreateCompetitionCategory() {
        // Setup
        val category = categoryRepository.getAvailableCategories().find { it.name == "Herrar 1" }!!
        val categorySpec = CategorySpec(category.id, category.name, category.type)
        val club = addClub.execute(dataGenerator.newClubSpec(name = "Svenska Klubben"))
        val competition = addCompetition.execute(dataGenerator.newCompetitionSpec(organizingClubId = club.id))

        // Act
        val competitionCategory = testRestTemplate.postForObject(
            getUrl() + "/${competition.id}/category",
            HttpEntity(categorySpec, getAuthenticationHeaders()),
            CompetitionCategoryDTO::class.java
        )

        // Clean up
        repository.delete(competitionCategory.id)
        competitionRepository.deleteCompetition(competition.id)
        deleteClub.execute(club.id)
    }
}