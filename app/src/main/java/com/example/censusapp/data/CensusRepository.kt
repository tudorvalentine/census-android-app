package com.example.censusapp.data

import com.example.censusapp.model.CivilStateData
import com.example.censusapp.model.GenderData
import com.example.censusapp.model.GroupAgeData
import com.example.censusapp.model.NationalityAndNativeLang
import com.example.censusapp.model.CensusQuestionnaire
import com.example.censusapp.network.CensusApiService
import org.json.JSONObject
import retrofit2.Response

interface CensusRepository {
    suspend fun postAnswerQuestionnaire(data : CensusQuestionnaire) : Response<JSONObject>
    suspend fun getGenderData() : GenderData
    suspend fun getGroupedAges() : GroupAgeData
    suspend fun getCivilStateData() : CivilStateData
    suspend fun getNationalityData() : List<NationalityAndNativeLang>
}

class NetworkCensusRepository(
    private val censusApiService: CensusApiService,
) : CensusRepository{
    override suspend fun postAnswerQuestionnaire(censusQuestionnaire: CensusQuestionnaire) : Response<JSONObject> =
        censusApiService.postAnswer(censusQuestionnaire)

    override suspend fun getGenderData(): GenderData = censusApiService.getGender()

    override suspend fun getGroupedAges(): GroupAgeData = censusApiService.getGroupAgeCount()

    override suspend fun getCivilStateData(): CivilStateData = censusApiService.getCivilStateStatistics()

    override suspend fun getNationalityData(): List<NationalityAndNativeLang> = censusApiService.getNationality()
}