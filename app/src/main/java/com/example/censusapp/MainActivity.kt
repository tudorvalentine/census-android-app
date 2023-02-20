package com.example.censusapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.navigation.compose.rememberNavController
import com.example.censusapp.ui.CensusApp
import com.example.censusapp.ui.theme.RecensamantAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecensamantAppTheme {
                // A surface container using the 'background' color from the theme
                Surface {
                    val navController = rememberNavController()
                    CensusApp(navController = navController)
                }
            }
        }
    }
}