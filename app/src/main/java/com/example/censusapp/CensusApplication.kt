package com.example.censusapp

import android.app.Application
import com.example.censusapp.data.AppContainer
import com.example.censusapp.data.CensusAppContainer

class CensusApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = CensusAppContainer()
    }
}