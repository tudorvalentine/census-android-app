package com.example.censusapp.ui.screens

import android.graphics.Typeface
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.censusapp.R
import com.example.censusapp.model.CivilStateData
import com.example.censusapp.model.GenderData
import com.example.censusapp.model.GroupAgeData
import com.example.censusapp.ui.viewModels.StatisticsViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import hu.ma.charts.bars.HorizontalBarsChart
import hu.ma.charts.bars.data.HorizontalBarsData
import hu.ma.charts.bars.data.StackedBarData
import hu.ma.charts.bars.data.StackedBarEntry
import com.example.censusapp.navigation.NavRoute
import com.example.censusapp.ui.viewModels.StatisticsUiState

data class PieChartData(
    var gender: String?,
    var value: Float?
)

data class CardSource(
    @StringRes val title: Int,
    @DrawableRes val image: Int,
    @StringRes val description: Int,
    val nav : NavRoute,
    val getData : ()-> Unit
)

@Composable
fun StatisticsScreen(
    navController: NavHostController,
    statisticsViewModel: StatisticsViewModel
){
    val cardList = listOf(
        CardSource(
            title = R.string.gender,
            image = R.drawable.gender,
            description = R.string.genderDescription,
            nav = NavRoute.GenderStatistics,
            getData = {statisticsViewModel.getGenderStatistics()}
            ),
        CardSource(
            title = R.string.age_stat,
            image = R.drawable.age_stat,
            description = R.string.string_age_stat_description,
            nav = NavRoute.AgeStatistics,
            getData = {statisticsViewModel.getAges()}
            ),
        CardSource(
            title = R.string.civil_state,
            image = R.drawable.civil_state,
            description = R.string.civil_state_description,
            nav = NavRoute.CivilStateStatistics,
            getData = {statisticsViewModel.getCivilStateStatistics()}
            ),
        CardSource(
            title = R.string.nationality_stat,
            image = R.drawable.nationality,
            description = R.string.nationality_description,
            nav = NavRoute.NationalityAndNativeLang,
            getData = {statisticsViewModel.getEthnicData()}
        )

    )

    LazyColumn{
        items(cardList) {card->
            StatisticCard(cardSource = card, onClick = {
                navController.navigate(card.nav.route)
                card.getData()
            })
        }
    }
}
val SimpleColors = listOf(
    Color.Blue,
    Color.Yellow,
    Color.Red,
    Color.Magenta,
    Color.Cyan,
    Color.Green,
    Color.Gray,
)
@Composable
fun NationalityAndNativeLangScreen(
    navController: NavHostController,
    statisticsViewModel: StatisticsViewModel,
    statisticsUiState: StatisticsUiState
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(statisticsUiState){
            is StatisticsUiState.Loading -> LoadingScreen()
            is StatisticsUiState.Error -> ErrorScreen(errorMsg = statisticsUiState.errorMessage) {
                statisticsViewModel.getEthnicData()
            }
            is StatisticsUiState.SuccessList -> {
                BackArrowWithHeader(header = R.string.nationality_stat) {
                    navController.navigateUp()
                }
                val nationalityLabel = mutableListOf<String>()
                val nationalityPercentage = mutableListOf<List<Float>>()
                val nativeLangLabel = mutableListOf<String>()
                val nativeLangPercentage = mutableListOf<List<Float>>()

                val percentageDataNationality = mutableListOf<Float>()
                val percentageDataNativeLang = mutableListOf<Float>()

                for (row in statisticsUiState.data){
                    percentageDataNationality.add(row.nationalityPercentage)
                    percentageDataNativeLang.add(row.limbaMaternaPercentage)
                    nationalityLabel.add(row.nationality)
                    nativeLangLabel.add(row.limbaMaterna)
                }
                nationalityPercentage.add(percentageDataNationality)
                nativeLangPercentage.add(percentageDataNativeLang)

                val stackedBarDataNationalityPercentage = nationalityPercentage.map { values ->
                    StackedBarData(
                        title = AnnotatedString(""),
                        entries = values.mapIndexed { index, value ->
                            StackedBarEntry(
                                text = AnnotatedString(nationalityLabel[index]),
                                value = value,
                                color = SimpleColors[index]
                            )
                        }
                    )
                }
                val stackedBarDataNativeLangPercentage = nativeLangPercentage.map { values ->
                    StackedBarData(
                        title = AnnotatedString(""),
                        entries = values.mapIndexed { index, value ->
                            StackedBarEntry(
                                text = AnnotatedString(nativeLangLabel[index]),
                                value = value,
                                color = SimpleColors[index]
                            )
                        }
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    StackedBarChart(data = stackedBarDataNationalityPercentage, label = "Naționalități")
                    StackedBarChart(data = stackedBarDataNativeLangPercentage, label = "Limba maternă vorbită")
                }
            }

            else -> {}
        }
    }
}
@Composable
private fun StackedBarChart(
    data : List<StackedBarData>,
    label : String,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            HorizontalBarsChart(data = HorizontalBarsData(data))

        }
    }
}
@Composable
fun GenderStatisticsScreen(
    navController: NavHostController,
    statisticsViewModel: StatisticsViewModel,
    statisticsUiState: StatisticsUiState
    ){
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

        when(statisticsUiState){
            is StatisticsUiState.Loading -> LoadingScreen()
            is StatisticsUiState.Success ->{
                BackArrowWithHeader(R.string.gender) {
                    navController.navigateUp()
                }
                var percentageMale : Float = 0.0F
                var percentageFemale : Float = 0.0F
                if (statisticsUiState.data is GenderData){
                    percentageMale = statisticsUiState.data.percentageMale
                    percentageFemale = statisticsUiState.data.percentageFemale
                }
                val pieChartData = listOf(
                    PieChartData("Masculin", percentageMale),
                    PieChartData("Feminin", percentageFemale),
                )
                PieCharts(data = pieChartData)
            }
            is StatisticsUiState.Error -> ErrorScreen(errorMsg = statisticsUiState.errorMessage) {
                statisticsViewModel.getGenderStatistics()
            }

            else -> {}
        }
    }
}

@Composable
fun AgeStatisticsScreen(
    navController: NavHostController,
    statisticsViewModel: StatisticsViewModel,
    statisticsUiState: StatisticsUiState
    ){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        when(statisticsUiState){
            is StatisticsUiState.Loading -> LoadingScreen()
            is StatisticsUiState.Error -> ErrorScreen(errorMsg = statisticsUiState.errorMessage) {
                statisticsViewModel.getAges()
            }
            is StatisticsUiState.Success -> {

                BackArrowWithHeader(header = R.string.age_stat) {
                    navController.navigateUp()
                }
                if (statisticsUiState.data is GroupAgeData){
                    val dataEntry1 = mutableListOf(
                        BarEntry(0f, statisticsUiState.data.group_0_14),
                    )
                    val set1 = BarDataSet(
                        dataEntry1,
                        "0 - 14 ani")
                    val dataEntry2 = mutableListOf(
                        BarEntry(1f, statisticsUiState.data.group_15_59),
                    )
                    set1.color = Color.Green.toArgb()
                    val set2 = BarDataSet(
                        dataEntry2,
                        "15 - 59 ani")
                    set2.color = Color.Cyan.toArgb()
                    val dataEntry3 = mutableListOf(
                        BarEntry(2f, statisticsUiState.data.group_60_more),
                    )
                    val set3 = BarDataSet(
                        dataEntry3,
                        "60 + ani")
                    set3.color = Color.LightGray.toArgb()
                    val barData = BarData(set1, set2, set3)
                    AgeBarChart(barData)
                }

            }

            else -> {}
        }
    }
}

@Composable
fun CivilStateStatisticsScreen(
    navController: NavHostController,
    statisticsViewModel: StatisticsViewModel,
    statisticsUiState: StatisticsUiState
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when(statisticsUiState){
            is StatisticsUiState.Loading -> LoadingScreen()
            is StatisticsUiState.Error -> ErrorScreen(errorMsg = statisticsUiState.errorMessage) {
                statisticsViewModel.getCivilStateStatistics()
            }
            is StatisticsUiState.Success -> {
                BackArrowWithHeader(header = R.string.civil_state) {
                    navController.navigateUp()
                }

                if (statisticsUiState.data is CivilStateData){
                    val pieChartData = listOf(
                        PieChartData("Necăsătoriți", statisticsUiState.data.percentageUnmarried),
                        PieChartData("Căsătoriți", statisticsUiState.data.percentageMarried),
                        PieChartData("Văduvi", statisticsUiState.data.percentageWidower),
                        PieChartData("Divorțați", statisticsUiState.data.percentageDivorced),
                    )
                    PieCharts(data = pieChartData)
                }
            }

            else -> {}
        }
    }
}
@Composable
fun BackArrowWithHeader(
    @StringRes header : Int,
    back : ()-> Unit
){
    Row(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            modifier = Modifier.padding(6.dp),
            onClick = back,
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
        }

        Text(
            text = stringResource(id = header),
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
            )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticCard(
    modifier: Modifier = Modifier,
    cardSource: CardSource,
    onClick : () -> Unit
){
    Card(
        onClick = onClick,
        //shape = MaterialTheme.shapes.medium,
        shape = RoundedCornerShape(16.dp),
        // modifier = modifier.size(280.dp, 240.dp)
        modifier = Modifier.padding(10.dp,5.dp,10.dp,10.dp),
        //set card elevation of the card
        elevation = CardDefaults.cardElevation(
            defaultElevation =  10.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor =  MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column {
            Image(
                painter = painterResource(cardSource.image),
                contentDescription = null, // decorative
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = cardSource.title),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = stringResource(id = cardSource.description),
                    //maxLines = 1,
                    //overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun AgeBarChart(
    dataBar : BarData
){
    Column(modifier = Modifier
        .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Crossfade(targetState = dataBar) { bar ->
            AndroidView(
                factory = { context ->
                    BarChart(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            // on below line we are specifying layout
                            // params as MATCH PARENT for height and width.
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        this.animateY(1500)

                    }
                },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(5.dp),
                update = {
                    updateBarDate(it, bar)
            }
            )
        }
    }
}
fun updateBarDate(
    barChart: BarChart,
    data: BarData
){
    /*data.colors = arrayListOf(
        Color.Green.toArgb(),
        Color.Red.toArgb(),
        Color.Magenta.toArgb()
    )*/
    data.setValueFormatter(object : ValueFormatter(){
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
        }
    })
    barChart.data = data

    barChart.description.isEnabled = false

    val xAxis = barChart.xAxis
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.setDrawGridLines(false)
    xAxis.setDrawAxisLine(true)
    xAxis.granularity = 1f
    xAxis.labelCount = 3
    xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("0 - 14", "15 - 59", "60 +"))

    val leftAxis = barChart.axisLeft
    leftAxis.setDrawGridLines(true)
    leftAxis.setDrawAxisLine(true)
    leftAxis.axisMinimum = 0f
    leftAxis.granularity = 1f

    val rightAxis = barChart.axisRight
    rightAxis.setDrawGridLines(false)
    rightAxis.setDrawAxisLine(false)
    rightAxis.axisMinimum = 0f
    rightAxis.granularity = 1f

    val legend = barChart.legend
    legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
    legend.orientation = Legend.LegendOrientation.HORIZONTAL
    legend.setDrawInside(false)
    legend.yOffset = 5f
    legend.xOffset = 0f
    legend.yEntrySpace = 0f
    legend.textSize = 12f

}
@Composable
fun PieCharts(
    data : List<PieChartData>
) {
    Column(
        modifier = Modifier
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // on below line we are creating a cross fade and
        // specifying target state as pie chart data the
        // method we have created in Pie chart data class.
        Crossfade(targetState = data) { pieChartData ->
            // on below line we are creating an
            // android view for pie chart.
            AndroidView(factory = { context ->
                // on below line we are creating a pie chart
                // and specifying layout params.
                PieChart(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        // on below line we are specifying layout
                        // params as MATCH PARENT for height and width.
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    // on below line we are setting description
                    // enables for our pie chart.
                    this.description.isEnabled = false
                    this.animateX(700)
                    this.animateY(700)
                    // on below line we are setting draw hole
                    // to false not to draw hole in pie chart
                    //this.isDrawHoleEnabled = false

                    // on below line we are enabling legend.
                    this.legend.isEnabled = true

                    // on below line we are specifying
                    // text size for our legend.
                    this.legend.textSize = 14F

                    // on below line we are specifying
                    // alignment for our legend.
                    this.legend.horizontalAlignment =
                        Legend.LegendHorizontalAlignment.CENTER

                    // on below line we are specifying entry label color as white.
                    this.setEntryLabelColor(resources.getColor(R.color.black))
                }
            },
                // on below line we are specifying modifier
                // for it and specifying padding to it.
                modifier = Modifier
                    .wrapContentSize()
                    .padding(5.dp), update = {
                    // on below line we are calling update pie chart
                    // method and passing pie chart and list of data.
                    updatePieChartWithData(it, pieChartData)
                })
        }
    }
}

// on below line we are creating a update pie
// chart function to update data in pie chart.
fun updatePieChartWithData(
    // on below line we are creating a variable
    // for pie chart and data for our list of data.
    chart: PieChart,
    data: List<PieChartData>
) {
    // on below line we are creating
    // array list for the entries.
    val entries = ArrayList<PieEntry>()

    // on below line we are running for loop for
    // passing data from list into entries list.
    for (i in data.indices) {
        val item = data[i]
        entries.add(PieEntry(item.value ?: 0.toFloat(), item.gender ?: ""))
    }

    // on below line we are creating
    // a variable for pie data set.

    val ds = PieDataSet(entries, "")
    // on below line we are specifying color
    // int the array list from colors.
    ds.colors = arrayListOf(
        Color.Green.toArgb(),
        Color.Red.toArgb(),
        Color.Yellow.toArgb(),
        Color.Cyan.toArgb(),
    )
    // on below line we are specifying position for value
    ds.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

    // on below line we are specifying position for value inside the slice.
    ds.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

    // on below line we are specifying
    // slice space between two slices.
    ds.sliceSpace = 10f

    // on below line we are specifying text color
    ds.valueTextColor = Color.Black.toArgb()

    // on below line we are specifying
    // text size for value.
    ds.valueTextSize = 24f

    // on below line we are specifying type face as bold.
    ds.valueTypeface = Typeface.DEFAULT_BOLD

    // on below line we are creating
    // a variable for pie data
    val d = PieData(ds)

    // on below line we are setting this
    // pie data in chart data.
    chart.data = d

    // on below line we are
    // calling invalidate in chart.
    chart.invalidate()
}
@Composable
fun LoadingScreen(){
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    Box(
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = R.drawable.loader, imageLoader),
                contentDescription = "Loading . . .")
        }
    }
}
@Composable
fun ErrorScreen(
    errorMsg : String,
    retryAction : ()-> Unit
){
    Box(
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(painter = painterResource(id = R.drawable.icons8_cancel_80), contentDescription = "Error load")
            Text(text = errorMsg)
            Button(modifier = Modifier.padding(16.dp),onClick = {
                retryAction()
                Log.d("test", "Clicked")
            }) {
                Text(text = "Mai încearcă", modifier = Modifier.padding(8.dp))
            }
        }
    }
}
