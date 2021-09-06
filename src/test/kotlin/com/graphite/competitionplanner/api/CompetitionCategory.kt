package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.AbstractApiTest
import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.domain.dto.CategoryDTO
import com.graphite.competitionplanner.domain.dto.CompetitionCategoryDTO
import com.graphite.competitionplanner.domain.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.domain.usecase.club.CreateClub
import com.graphite.competitionplanner.domain.usecase.club.DeleteClub
import com.graphite.competitionplanner.domain.usecase.competition.CreateCompetition
import com.graphite.competitionplanner.repositories.competition.CompetitionRepository
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
    @Autowired val competitionRepository: CompetitionRepository
) : AbstractApiTest(
    port,
    testRestTemplate
) {
    override val resource: String = "/competition"
    val dataGenerator = DataGenerator()

    @Test
    fun canCreateCompetitionCategory() {
        // Setup
        val category = repository.getAvailableCategories().find { it.name == "Herrar 1" }!!
        val categorySpec = CategoryDTO(category.id, category.name, category.type)
        val club = addClub.execute(dataGenerator.newClubDTO(id = 0, name = "Svenska Klubben"))
        val competition = addCompetition.execute(dataGenerator.newNewCompetitionDTO(organizingClubId = club.id))

        // Act
        val competitionCategory = testRestTemplate.postForObject(
            getUrl() + "/${competition.id}/category",
            HttpEntity(categorySpec, getAuthenticationHeaders()),
            CompetitionCategoryDTO::class.java
        )

        // Clean up
        repository.delete(competitionCategory.id)
        competitionRepository.deleteCompetition(competition.id)
        deleteClub.execute(club)
    }
}