package com.example.censusapp.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.censusapp.R
import com.example.censusapp.ui.viewModels.QuestionnaireViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import com.example.censusapp.ui.viewModels.QuestionnaireUiState

@Composable
fun QuestionnaireScreen(
    questionnaireViewModel: QuestionnaireViewModel = viewModel(),
    questionnaireUiState: QuestionnaireUiState,
    retryAction : () -> Unit
){
    val gender = listOf("Masculin", "Feminin")
    val civilState = listOf("Necăsătorit(ă)", "Casătorit(ă)", "Văduv(ă)", "Divorțat(ă)")
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    Surface(color = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface) {
        Column(modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()
            .padding(16.dp))
        {

            DividerWithHeader(text = R.string.personal_data)
            InputQuestion(
                modifier = Modifier,
                question = R.string.name,
                supportingText = questionnaireViewModel.validateInputName.errorMessage,
                label = stringResource(id = R.string.name),
                value = questionnaireViewModel.inputName,
                isError = questionnaireViewModel.validateInputName.isError,
                onValueChange = {
                    questionnaireViewModel.updateInputName(it)
                    questionnaireViewModel.checkInputName()
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )
            Divider()
            InputQuestion(
                modifier = Modifier,
                question = R.string.prenume,
                supportingText = questionnaireViewModel.validateLastName.errorMessage,
                label = stringResource(id = R.string.prenume),
                value = questionnaireViewModel.inputLastName,
                isError = questionnaireViewModel.validateLastName.isError,
                onValueChange = {
                    questionnaireViewModel.updateInputLastName(it)
                    questionnaireViewModel.checkLastName()
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )
            DividerWithHeader(text = R.string.demografice)
            RadioQuestion(
                question = R.string.sex_ul,
                options = gender,
                onClick = {
                    questionnaireViewModel.radioUpdateSelectedGender(it)
                    questionnaireViewModel.checkGenderRadio()
                },
                error = questionnaireViewModel.validateRadioSelectedGender.isError,
                errorMessage = questionnaireViewModel.validateRadioSelectedGender.errorMessage,
                selected = questionnaireViewModel.radioSelectedGender
            )
            Divider()
            DatePicker(
                mContext = LocalContext.current,
                date = questionnaireViewModel.dateOfBorn,
                updateDate = {
                    questionnaireViewModel.updateDateOfBorn(it)
                    questionnaireViewModel.checkDateOfBorn()
                },
                error = questionnaireViewModel.validateDateOfBorn.isError,
                errorMessage = questionnaireViewModel.validateDateOfBorn.errorMessage
            )
            Spacer(modifier = Modifier.size(16.dp))
            Divider()
            Spacer(modifier = Modifier.size(16.dp))
            RadioQuestion(
                question = R.string.starea_civil_legal,
                options = civilState,
                onClick = {
                    questionnaireViewModel.radioUpdateSelectedCivilState(it)
                    questionnaireViewModel.checkCivilStateRadio()
                },
                selected = questionnaireViewModel.radioSelectedCivilState,
                error = questionnaireViewModel.validateRadioSelectedCivilState.isError,
                errorMessage = questionnaireViewModel.validateRadioSelectedCivilState.errorMessage,
            )
            Spacer(modifier = Modifier.size(16.dp))
            InputQuestion(
                modifier = Modifier,
                question = R.string.numar_copii,
                supportingText = questionnaireViewModel.validateInputNumOfChildren.errorMessage,
                label = stringResource(id = R.string.numar_copii_label),
                value = questionnaireViewModel.inputNumOfChildren,
                isError = questionnaireViewModel.validateInputNumOfChildren.isError,
                onValueChange = {
                    questionnaireViewModel.updateInputNumOfChildren(it)
                    questionnaireViewModel.checkInputNumOfChildren()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
            Divider()
            Spacer(modifier = Modifier.size(16.dp))
            DividerWithHeader(text = R.string.etnoculturale)
            InputQuestion(
                modifier = Modifier,
                question = R.string.nationality,
                supportingText = questionnaireViewModel.validateInputNationality.errorMessage,
                label = stringResource(R.string.na_ionalitate),
                value = questionnaireViewModel.inputNationality,
                isError = questionnaireViewModel.validateInputNationality.isError,
                onValueChange = {
                    questionnaireViewModel.updateInputNationality(it)
                    questionnaireViewModel.checkInputNationality()
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )
            Divider()
            InputQuestion(
                modifier = Modifier,
                question = R.string.limba_matern,
                supportingText = questionnaireViewModel.validateInputLanguage.errorMessage,
                label = stringResource(R.string.limba_matern),
                value = questionnaireViewModel.inputLanguage,
                isError = questionnaireViewModel.validateInputLanguage.isError,
                onValueChange = {
                    questionnaireViewModel.updateInputLanguage(it)
                    questionnaireViewModel.checkInputLanguage()
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )

            )
            Divider()
            Spacer(modifier = Modifier.size(16.dp))
            DividerWithHeader(text = R.string.economice_header)
            Text(text = stringResource(id = R.string.sursa_de_venit), fontWeight = FontWeight.Bold)
            if (questionnaireViewModel.validateListOfCheckBoxes.isError){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = questionnaireViewModel.validateListOfCheckBoxes.errorMessage,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }
            }
            questionnaireViewModel.listOfCheckBoxes.forEachIndexed{index,item->
                Row(
                    Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = item.checkBoxValue,
                        onCheckedChange = {
                            questionnaireViewModel.updateStatesOfCheckBox(index, it)
                            questionnaireViewModel.checkCheckboxes()
                        }
                    )
                    Text(
                        text = item.checkBoxContent,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Divider()
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        questionnaireViewModel.postAnswer()
                        questionnaireViewModel.resetAllState()
                        scope.launch {
                            scrollState.animateScrollTo(0)
                        }

                    },
                    enabled = questionnaireViewModel.isEnabledSubmitButton
                ) {
                    Text(text = "Trimite chestionarul")
                }
            }

            when(questionnaireUiState){
                is QuestionnaireUiState.Loading -> CustomDialog(
                    dialogImage = R.drawable.loader,
                    questionnaireUiState.show
                )
                is QuestionnaireUiState.Error ->
                    if (questionnaireUiState.show){
                        AlertDialog(
                            onDismissRequest = {
                                // Dismiss the dialog when the user clicks outside the dialog or on the back
                                // button. If you want to disable that functionality, simply use an empty
                                // onDismissRequest.
                                questionnaireViewModel.closeErrorDialog()
                            },
                            icon = { Icon(Icons.Filled.Refresh, contentDescription = null) },
                            title = {
                                Text(text = "Reîncercați")
                            },
                            text = {
                                Text(text = questionnaireUiState.errorMessage)
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        retryAction
                                    }
                                ) {
                                    Text("Confirm")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        questionnaireViewModel.closeErrorDialog()
                                    }
                                ) {
                                    Text("Închide")
                                }
                            }
                        )
                    }
                is QuestionnaireUiState.Success -> CustomDialog(
                    dialogImage = R.drawable.checked,
                    questionnaireUiState.show
                )
                is QuestionnaireUiState.Default -> CustomDialog(dialogImage = R.drawable.checked, show = false)
            }
        }
    }

}

@Composable
fun CustomDialog(
    @DrawableRes dialogImage: Int,
    show : Boolean
){
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    if (show){
        Dialog(onDismissRequest = {}) {
            Surface(
                modifier = Modifier
                    .size(200.dp)
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
                    .wrapContentHeight(align = Alignment.CenterVertically),
                shape = RoundedCornerShape(25.dp)
            ) {
                Column(modifier = Modifier.padding(all = 8.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(model = dialogImage, imageLoader),
                        contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun DividerWithHeader(text: Int) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.titleLarge
        )
    }
    Divider()
    Spacer(modifier = Modifier.size(16.dp))
}

@Composable
fun RadioQuestion(
    modifier: Modifier = Modifier,
    @StringRes question : Int,
    options : List<String>,
    selected : String,
    error : Boolean,
    errorMessage : String,
    onClick : (String) -> Unit
){
    //var selected by rememberSaveable { mutableStateOf("") }
    Text(
        text = stringResource(question),
        fontWeight = FontWeight.Bold
    )
    if (error){
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = errorMessage,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color.Red
            )
        }
    }
    options.forEach{option->
        Row( verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected == option, onClick = {onClick(option)})
            Text(
                text = option,
                modifier = Modifier
                    .clickable(onClick = { onClick(option) })
                    .padding(start = 4.dp)
            )
        }
    }


}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputQuestion(
    modifier: Modifier,
    @StringRes question: Int,
    supportingText: String,
    label : String = "",
    value : String,
    isError : Boolean,
    keyboardOptions : KeyboardOptions,
    onValueChange : (String) -> Unit
){
    val focusManager = LocalFocusManager.current
    Spacer(modifier = Modifier.size(8.dp))
    Text(text = stringResource(id = question), fontWeight = FontWeight.Bold)
    OutlinedTextField(
        supportingText = { Text(text = supportingText ) },
        singleLine = true,
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(onNext = {
            focusManager.moveFocus(FocusDirection.Down)
        })
    )
    Spacer(modifier = Modifier.size(8.dp))
}
@Composable
fun DatePicker(
    mContext : Context,
    date : String,
    error: Boolean,
    errorMessage: String,
    updateDate : (String) -> Unit
){
    // Fetching the Local Context

    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,0,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            //updateDate("$mDayOfMonth/${mMonth+1}/$mYear")
            updateDate("$mYear-${mMonth+1}-$mDayOfMonth")
        }, mYear, mMonth, mDay
    )

    //deactivate the future dates in DatePickerDialor
    mDatePickerDialog.datePicker.maxDate = System.currentTimeMillis()

    Spacer(modifier = Modifier.size(16.dp))
    Text(text = "Data nașterii :", fontWeight = FontWeight.Bold)
    if (error){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = errorMessage,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color.Red
            )
        }
    }

    // Creating a button that on
    // click displays/shows the DatePickerDialog
    Button(onClick = {
        mDatePickerDialog.show()
    }) {
        Text(text = "Alege data nașterii", color = Color.White)
    }
    if (date != "")
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            text = "Data de naștere aleasă este : $date",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )



    // Displaying the mDate value in the Text
//        Text(text = "Selected Date: ${mDate.value}", fontSize = 30.sp, textAlign = TextAlign.Center)
}