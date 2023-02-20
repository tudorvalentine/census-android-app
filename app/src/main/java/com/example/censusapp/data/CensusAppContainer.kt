package com.example.censusapp.data

import com.example.censusapp.network.CensusApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer{
    val censusRepository : CensusRepository
}


class CensusAppContainer : AppContainer{
    private val BASE_URL = "http://192.168.0.53:8080/"

    //Retrofit Builder
    private val retrofit : Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitService : CensusApiService by lazy {
        retrofit.create(CensusApiService::class.java)
    }
    override val censusRepository: CensusRepository by lazy {
        NetworkCensusRepository(retrofitService)
    }
}