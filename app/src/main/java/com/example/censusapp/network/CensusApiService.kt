package com.example.censusapp.network

import com.example.censusapp.model.CivilStateData
import com.example.censusapp.model.GenderData
import com.example.censusapp.model.GroupAgeData
import com.example.censusapp.model.NationalityAndNativeLang
import com.example.censusapp.model.CensusQuestionnaire
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CensusApiService {


    @POST("/app/answers")
    suspend fun postAnswer(@Body data: CensusQuestionnaire) : Response<JSONObject>

    @GET("/app/gender-data")
    suspend fun getGender() : GenderData

    @GET("/app/ages-data")
    suspend fun getGroupAgeCount() : GroupAgeData

    @GET("/app/civil-state-data")
    suspend fun getCivilStateStatistics() : CivilStateData

    @GET("/app/nationality-data")
    suspend fun getNationality() : List<NationalityAndNativeLang>
}