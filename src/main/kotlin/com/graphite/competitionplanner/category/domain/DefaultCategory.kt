package com.graphite.competitionplanner.category.domain

/**
 * The default categories that are available for all competition
 */
enum class DefaultCategory(val type: CategoryType)
{
    MEN_1(CategoryType.SINGLES),
    MEN_2(CategoryType.SINGLES),
    MEN_3(CategoryType.SINGLES),
    MEN_4(CategoryType.SINGLES),
    MEN_5(CategoryType.SINGLES),
    MEN_6(CategoryType.SINGLES),
    WOMEN_1(CategoryType.SINGLES),
    WOMEN_2(CategoryType.SINGLES),
    WOMEN_3(CategoryType.SINGLES),
    WOMEN_4(CategoryType.SINGLES),
    WOMEN_JUNIOR_17(CategoryType.SINGLES),
    GIRLS_15(CategoryType.SINGLES),
    GIRLS_14(CategoryType.SINGLES),
    GIRLS_13(CategoryType.SINGLES),
    GIRLS_12(CategoryType.SINGLES),
    GIRLS_11(CategoryType.SINGLES),
    GIRLS_10(CategoryType.SINGLES),
    GIRLS_9(CategoryType.SINGLES),
    GIRLS_8(CategoryType.SINGLES),
    MEN_JUNIOR_17(CategoryType.SINGLES),
    BOYS_15(CategoryType.SINGLES),
    BOYS_14(CategoryType.SINGLES),
    BOYS_13(CategoryType.SINGLES),
    BOYS_12(CategoryType.SINGLES),
    BOYS_11(CategoryType.SINGLES),
    BOYS_10(CategoryType.SINGLES),
    BOYS_9(CategoryType.SINGLES),
    BOYS_8(CategoryType.SINGLES),
    MEN_TEAMS(CategoryType.DOUBLES),
    WOMEN_TEAMS(CategoryType.DOUBLES)
}