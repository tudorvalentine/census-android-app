package com.example.censusapp.model

import com.example.censusapp.ui.viewModels.CheckBoxUiState
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CensusQuestionnaire(
    @SerializedName("name") val name: String = "",
    @SerializedName("last_name") val lastName: String = "",
    @SerializedName("gender") val gender: String = "",
    @SerializedName("born_date") val bornDate: String = "",
    @SerializedName("civil_state") val civilState: String = "",
    @SerializedName("nr_children") val numOfChildren : Int = 0,
    @SerializedName("nationality") val nationality: String = "",
    @SerializedName("native_language") val nativeLanguage: String = "",
    @SerializedName("income") val income : List<CheckBoxUiState>? = null
)
@Serializable
open class BaseStatisticsData

@Serializable
data class GenderData(
    @SerializedName("count_male")
    val countMale : Long ,
    @SerializedName("count_female")
    val countFemale : Long,
    @SerializedName("percentage_male")
    val percentageMale : Float,
    @SerializedName("percentage_female")
    val percentageFemale : Float,
) : BaseStatisticsData()
@Serializable
data class GroupAgeData(
    @SerializedName("count_age_group_0_14")
    val group_0_14 : Float,
    @SerializedName("count_age_group_15_59")
    val group_15_59: Float,
    @SerializedName("count_age_group_60_more")
    val group_60_more: Float,

) : BaseStatisticsData()

data class CivilStateData(
    @SerializedName("percentage_unmarried")
    val percentageUnmarried : Float,
    @SerializedName("percentage_married")
    val percentageMarried: Float,
    @SerializedName("percentage_widower")
    val percentageWidower: Float,
    @SerializedName("percentage_divorced")
    val percentageDivorced: Float,
) : BaseStatisticsData()

data class NationalityAndNativeLang(
    @SerializedName("nationality_label")
    val nationality: String,
    @SerializedName("nationality_count")
    val nationalityPercentage : Float,
    @SerializedName("limba_materna_label")
    val limbaMaterna : String,
    @SerializedName("limba_materna_count")
    val limbaMaternaPercentage : Float
)