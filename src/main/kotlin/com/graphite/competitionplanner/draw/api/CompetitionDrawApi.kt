package com.graphite.competitionplanner.draw.api

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.draw.domain.*
import com.graphite.competitionplanner.draw.interfaces.ApproveSeedingSpec
import com.graphite.competitionplanner.draw.interfaces.CompetitionCategoryDrawDTO
import com.graphite.competitionplanner.draw.interfaces.RegistrationSeedDTO
import com.graphite.competitionplanner.draw.service.DrawService
import com.graphite.competitionplanner.draw.service.GroupDrawDTO
import com.graphite.competitionplanner.draw.service.PlayoffDTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("competition/{competitionId}/draw/{competitionCategoryId}")
class CompetitionDrawApi(
    val createDraw: CreateDraw,
    val drawService: DrawService,
    val getDraw: GetDraw,
    val deleteDraw: DeleteDraw,
    val getCurrentSeeding: GetCurrentSeeding,
    val approveSeeding: ApproveSeeding,
    val findCompetitionCategory: FindCompetitionCategory
) {

    // Can be used both to create initial draw and to make a new draw if desired
    @PutMapping
    fun makeDraw(@PathVariable competitionCategoryId: Int): CompetitionCategoryDrawDTO {
        return createDraw.execute(competitionCategoryId)
    }

    @GetMapping
    fun getDraw(@PathVariable competitionCategoryId: Int): CompetitionCategoryDrawDTO {
        return getDraw.execute(competitionCategoryId)
    }

    @GetMapping("/pool")
    fun getPoolDraw(@PathVariable competitionCategoryId: Int) {
    }

    @GetMapping("/playoffs")
    fun getPlayoffDraw(@PathVariable competitionCategoryId: Int) {
    }

    @DeleteMapping
    fun deleteDraw(@PathVariable competitionCategoryId: Int) {
        deleteDraw.execute(competitionCategoryId)
    }

    @GetMapping("/is-draw-made")
    fun isDrawMade(@PathVariable competitionCategoryId: Int): Boolean {
        return drawService.isDrawMade(competitionCategoryId)
    }

    @GetMapping("seeding")
    fun getCurrentSeeding(@PathVariable competitionCategoryId: Int): List<RegistrationSeedDTO> {
        val competitionCategory = findCompetitionCategory.byId(competitionCategoryId)
        return getCurrentSeeding.execute(competitionCategory)
    }

    @PostMapping("seeding")
    fun approveSeeding(
        @PathVariable competitionCategoryId: Int,
        @RequestBody spec: ApproveSeedingSpec)
    {
        val competitionCategory = findCompetitionCategory.byId(competitionCategoryId)
        approveSeeding.execute(competitionCategory, spec)
    }

}

data class DrawDTO(
    val groupDraw: GroupDrawDTO,
    val playOff: PlayoffDTO
)