package com.example.censusapp.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.censusapp.CensusApplication
import com.example.censusapp.data.CensusRepository
import com.example.censusapp.model.BaseStatisticsData
import com.example.censusapp.model.NationalityAndNativeLang
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface StatisticsUiState {
    data class Success(val data : BaseStatisticsData) : StatisticsUiState
    data class SuccessList(val data : List<NationalityAndNativeLang>) : StatisticsUiState
    data class Error(val errorMessage: String) : StatisticsUiState
    object Loading : StatisticsUiState
}

class StatisticsViewModel (
    private val censusRepository: CensusRepository,
) : ViewModel(){
    val TAG = StatisticsViewModel::class.toString()
    var statisticsUiState : StatisticsUiState by mutableStateOf(StatisticsUiState.Loading)
        private set
    fun getEthnicData(){
        viewModelScope.launch {
            statisticsUiState = StatisticsUiState.Loading
            statisticsUiState = try {
                StatisticsUiState.SuccessList(censusRepository.getNationalityData())

            }catch (e: SocketTimeoutException){
                Log.d(TAG, "$e")
                StatisticsUiState.Error(
                    errorMessage = "A expirat timpul de așteptare. Mai încercați"
                )
            }catch (e: HttpException){
                Log.d(TAG, "$e")
                StatisticsUiState.Error("Server-ul nu lucrează. Mai încercați")
            }catch (e: IOException){
                Log.d(TAG, "$e")
                StatisticsUiState.Error("I/O error")
            }catch (e: Exception) {
                Log.d(TAG, "$e")
                StatisticsUiState.Error("Unknown error")
            }
        }
    }
    private fun getReq(
        getData : suspend ()-> BaseStatisticsData
    ){
        viewModelScope.launch {
            statisticsUiState = StatisticsUiState.Loading
            statisticsUiState = try {
                StatisticsUiState.Success(getData())

            }catch (e: SocketTimeoutException){
                Log.d(TAG, "$e")
                StatisticsUiState.Error(
                    errorMessage = "A expirat timpul de așteptare. Mai încercați"
                )
            }catch (e: HttpException){
                Log.d(TAG, "$e")
                StatisticsUiState.Error("Server-ul nu lucrează. Mai încercați")
            }catch (e: IOException){
                Log.d(TAG, "$e")
                StatisticsUiState.Error("I/O error")
            }catch (e: Exception) {
                Log.d(TAG, "$e")
                StatisticsUiState.Error("Unknown error")
            }
        }
    }

    fun getGenderStatistics(){
        getReq {
            censusRepository.getGenderData()
        }
    }
    fun getAges(){
        getReq {
            censusRepository.getGroupedAges()
        }
    }
    fun getCivilStateStatistics(){
        getReq {
            censusRepository.getCivilStateData()
        }
    }


    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =(this[APPLICATION_KEY] as CensusApplication)
                val censusRepository = application.container.censusRepository
                StatisticsViewModel(censusRepository = censusRepository)
            }
        }
    }
}