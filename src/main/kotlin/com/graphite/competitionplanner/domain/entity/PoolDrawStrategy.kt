package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.dto.PoolDrawStrategyDTO

data class PoolDrawStrategy(
    val id: Int,
    val name: String
) {
    constructor(dto: PoolDrawStrategyDTO) : this(dto.id, dto.name)
}
