package com.graphite.competitionplanner.competitioncategory.domain

/**
 * This exception is thrown if one tries to delete a competition category that has registered players. One is not
 * allowed to delete a competition category where there are registered players.
 */
class CannotDeleteCompetitionCategoryException : Exception("Cannot delete category. There are registered player in it.")