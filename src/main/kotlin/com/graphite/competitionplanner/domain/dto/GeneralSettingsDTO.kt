package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.GeneralSettings

data class GeneralSettingsDTO(
    val cost: Float,
    val drawType: DrawTypeDTO,
    val playersPerGroup: Int,
    val playersToPlayOff: Int,
    val poolDrawStrategy: PoolDrawStrategyDTO
) {
    constructor(entity: GeneralSettings) : this(
        entity.cost,
        DrawTypeDTO(entity.drawTypeId),
        entity.playersPerGroup,
        entity.playersToPlayOff,
        PoolDrawStrategyDTO(entity.poolDrawStrategy)
    )
}