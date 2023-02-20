package com.example.censusapp.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.censusapp.PDFViewerActivity
import com.example.censusapp.R
import com.example.censusapp.navigation.tabsNavigationScreens
import com.example.censusapp.ui.screens.AgeStatisticsScreen
import com.example.censusapp.ui.screens.CivilStateStatisticsScreen
import com.example.censusapp.ui.screens.GenderStatisticsScreen
import com.example.censusapp.ui.screens.NationalityAndNativeLangScreen
import com.example.censusapp.ui.screens.QuestionnaireScreen
import com.example.censusapp.ui.screens.StatisticsScreen
import com.example.censusapp.ui.viewModels.QuestionnaireViewModel
import com.example.censusapp.ui.viewModels.StatisticsViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.example.censusapp.navigation.NavRoute

@Composable
fun CensusApp(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBarComponent(navController) }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            val questionnaireViewModel : QuestionnaireViewModel = viewModel(factory = QuestionnaireViewModel.Factory)
            val statisticsViewModel : StatisticsViewModel = viewModel(factory = StatisticsViewModel.Factory)
            NavHost(
                navController = navController,
                startDestination = NavRoute.Questionnaire.route
            ){
                composable(route = NavRoute.Questionnaire.route){
                    QuestionnaireScreen(
                        questionnaireUiState = questionnaireViewModel.questionnaireUiState,
                        questionnaireViewModel = questionnaireViewModel,
                        retryAction = questionnaireViewModel::postAnswer
                    )
                }
                composable(route = NavRoute.Statistics.route){
                    StatisticsScreen(
                        navController = navController,
                        statisticsViewModel = statisticsViewModel
                    )
                }

                composable(route = NavRoute.GenderStatistics.route){
                    Surface(color = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface) {
                    }
                    GenderStatisticsScreen(navController, statisticsViewModel, statisticsViewModel.statisticsUiState)
                }

                composable(route = NavRoute.AgeStatistics.route){
                    AgeStatisticsScreen(navController, statisticsViewModel, statisticsViewModel.statisticsUiState)
                }

                composable(route = NavRoute.CivilStateStatistics.route){
                    CivilStateStatisticsScreen(navController, statisticsViewModel, statisticsViewModel.statisticsUiState)
                }

                composable(route = NavRoute.NationalityAndNativeLang.route){
                    Surface(color = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface) {
                        NationalityAndNativeLangScreen(
                            navController = navController,
                            statisticsViewModel = statisticsViewModel,
                            statisticsUiState = statisticsViewModel.statisticsUiState
                        )
                    }
                }
                /*composable(route = NavRoute.Guide.route){
                    GuideScreen()
                }*/
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun TopAppBarComponent(
    navController: NavHostController
){
    val mContext = LocalContext.current
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = tabsNavigationScreens.find { it.route == currentDestination?.route } ?: NavRoute.Statistics
    var currentIndex = tabsNavigationScreens.indexOf(currentScreen)
    var expanded by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    Column {
        CenterAlignedTopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.recens_m_nt), maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Image(
                        modifier = Modifier.size(64.dp),
                        painter = painterResource(id = R.drawable.logobns_normal),
                        contentDescription = null)
                }
            }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            actions = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Outlined.Info, contentDescription = null)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.ghid_de_utilizare)) },
                        onClick = {
                           /* val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse("http://192.168.8.128:8080/app/guide")
                            launcher.launch(intent)*/
                            mContext.startActivity(Intent(mContext, PDFViewerActivity::class.java))
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Help,
                                contentDescription = null
                            )
                        })
                }
            }
        )
        TabRow(
            selectedTabIndex = currentIndex,
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ) {
            tabsNavigationScreens.forEachIndexed{ index, screen ->
                Tab(
                    selected = currentScreen == screen,
                    onClick = {
                        currentIndex = index
                        navController.navigateSingleTopTo(screen.route)
                    },
                    icon = {
                        Icon(painter = painterResource(id = screen.icon), contentDescription = null)
                    },
                    text = {Text(text = screen.route, maxLines = 2, overflow = TextOverflow.Ellipsis)},
                )
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }