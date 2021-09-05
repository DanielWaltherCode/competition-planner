package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.PoolDrawStrategy

data class PoolDrawStrategyDTO(
    val name: String
) {
    constructor(entity: PoolDrawStrategy) : this(entity.name)
}