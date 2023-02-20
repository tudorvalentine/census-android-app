package com.example.censusapp.ui.viewModels

import com.google.gson.annotations.SerializedName


data class CheckBoxUiState(
    @SerializedName("check_box_content") val checkBoxContent : String = "",
    @SerializedName("check_box_value") var checkBoxValue : Boolean = false
)