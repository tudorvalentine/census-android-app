package com.example.censusapp.navigation

import androidx.annotation.DrawableRes
import com.example.censusapp.R


sealed class NavRoute(
    val route: String,
    @DrawableRes val  icon : Int = 0
){
    object Questionnaire : NavRoute("Chestionar", R.drawable.outline_question_mark_24)

    object Statistics: NavRoute("Statistică", R.drawable.baseline_query_stats_24)

    object GenderStatistics : NavRoute(route = "Statistica pe sexe")

    object AgeStatistics : NavRoute(route = "Statistica după vârstă a populației")

    object CivilStateStatistics : NavRoute(route = "Statistica după starea civilă")

    object NationalityAndNativeLang : NavRoute(route = "Limba maternă și Naționalitate")

    object Guide: NavRoute(route = "Guide")
}

val tabsNavigationScreens = listOf(NavRoute.Questionnaire, NavRoute.Statistics)



