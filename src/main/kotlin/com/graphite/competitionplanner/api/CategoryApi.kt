package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.repositories.CompetitionCategory
import com.graphite.competitionplanner.repositories.PlayingCategoryRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("category")
class CategoryApi(val playingCategoryRepository: PlayingCategoryRepository) {

    @GetMapping
    fun getCategories(): List<PlayingCategoryDTO> {
        val playingCategoryRecords = playingCategoryRepository.getCategories()
        return playingCategoryRecords.map { PlayingCategoryDTO(it.id, it.categoryName) }
    }


}

data class PlayingCategoryDTO(
    val categoryId: Int,
    val categoryName: String
)