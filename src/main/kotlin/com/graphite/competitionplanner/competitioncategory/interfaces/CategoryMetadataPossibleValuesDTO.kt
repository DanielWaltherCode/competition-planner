package com.graphite.competitionplanner.competitioncategory.interfaces

data class CategoryMetadataPossibleValuesDTO(
    val drawTypes: List<DrawType>,
    val drawStrategies: List<PoolDrawStrategy>
)