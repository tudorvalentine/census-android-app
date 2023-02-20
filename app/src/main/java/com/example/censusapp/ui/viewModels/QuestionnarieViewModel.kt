package com.example.censusapp.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.censusapp.model.CensusQuestionnaire
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface QuestionnaireUiState {
    data class Success(val show : Boolean) : QuestionnaireUiState
    data class Error(val show : Boolean, val errorMessage: String = "") : QuestionnaireUiState
    data class Loading(val show: Boolean) : QuestionnaireUiState
    object Default : QuestionnaireUiState
}
data class ValidationUiState(
    var isError : Boolean = false,
    var errorMessage : String = ""
)

class QuestionnaireViewModel(
    private val censusRepository: CensusRepository
) : ViewModel(){
    private val TAG = QuestionnaireViewModel::class.toString()
    private val _listOfCheckBoxes = mutableStateListOf(
        CheckBoxUiState(checkBoxContent = "salariu", checkBoxValue = false),
        CheckBoxUiState(checkBoxContent = "activitatea individuală agricolă, gospodăria auxiliară", checkBoxValue = false),
        CheckBoxUiState(checkBoxContent = "activitatea individuală non-agricolă (afacere proprie)", checkBoxValue = false),
        CheckBoxUiState(checkBoxContent = "pensie", checkBoxValue = false),
        CheckBoxUiState(checkBoxContent = "pensie de dizabilitate (invaliditate)", checkBoxValue = false),
        CheckBoxUiState(checkBoxContent = "ajutor de şomaj", checkBoxValue = false),
        CheckBoxUiState(checkBoxContent = "alte plăţi sociale (alocaţii sociale, compensaţii, indemnizaţii, ajutor social etc.)", checkBoxValue = false),
        CheckBoxUiState(checkBoxContent = "X burse", checkBoxValue = false),
        CheckBoxUiState(checkBoxContent = "transferuri din afara ţării", checkBoxValue = false),
        CheckBoxUiState(checkBoxContent = "venituri din proprietate", checkBoxValue = false),
        CheckBoxUiState(checkBoxContent = "altă sursă de venit", checkBoxValue = false),
        CheckBoxUiState(checkBoxContent = "la întreţinerea instituţiilor de stat", checkBoxValue = false),
        CheckBoxUiState(checkBoxContent = "la întreţinerea altor persoane", checkBoxValue = false),
    )

    val listOfCheckBoxes : List<CheckBoxUiState> = _listOfCheckBoxes
    var validateListOfCheckBoxes by mutableStateOf(ValidationUiState())
        private set

    var questionnaireUiState : QuestionnaireUiState by mutableStateOf(QuestionnaireUiState.Default)
    var answer = CensusQuestionnaire()


    // States for input fields
    var inputName by mutableStateOf(answer.name)
        private set
    var validateInputName by mutableStateOf(ValidationUiState())
        private set

    var inputLastName by mutableStateOf(answer.lastName)
        private set
    var validateLastName by mutableStateOf(ValidationUiState())
        private set

    var inputNationality by mutableStateOf(answer.nationality)
        private set
    var validateInputNationality by mutableStateOf(ValidationUiState())
        private set

    var inputLanguage by mutableStateOf(answer.nativeLanguage)
        private set
    var validateInputLanguage by mutableStateOf(ValidationUiState())
        private set

    // States for radio questions
    var radioSelectedGender by mutableStateOf(answer.gender)
        private set
    var validateRadioSelectedGender by mutableStateOf(ValidationUiState())
        private set

    var radioSelectedCivilState by mutableStateOf(answer.civilState)
        private set
    var validateRadioSelectedCivilState by mutableStateOf(ValidationUiState())
        private set

    var dateOfBorn by mutableStateOf(answer.bornDate)
        private set
    var validateDateOfBorn by mutableStateOf(ValidationUiState())
        private set
    var inputNumOfChildren by mutableStateOf("")
        private set
    var validateInputNumOfChildren by mutableStateOf(ValidationUiState())

    var isEnabledSubmitButton by mutableStateOf(false)
        private set

    init {
        resetAllState()
        validateRadioSelectedGender.isError = true
        validateRadioSelectedGender.errorMessage = "Nu este selectată nici o opțiune"

        validateDateOfBorn.isError = true
        validateDateOfBorn.errorMessage = "Selectați data de naștere"

        validateRadioSelectedCivilState.isError = true
        validateRadioSelectedCivilState.errorMessage = "Selectați starea civilă"

        validateListOfCheckBoxes.isError = true
        validateListOfCheckBoxes.errorMessage = "Minim o opțiune"
    }

    fun resetAllState(){
        inputLanguage = ""
        inputName = ""
        inputLastName = ""
        inputNationality = ""
        radioSelectedGender = ""
        radioSelectedCivilState = ""
        dateOfBorn = ""
        inputNumOfChildren = ""
        isEnabledSubmitButton = false
        listOfCheckBoxes.forEach{elem ->
            elem.checkBoxValue = false
        }
    }
    private fun shouldEnabledSubmitButton() {
        isEnabledSubmitButton =
            !validateInputName.isError && !validateInputLanguage.isError &&
            !validateLastName.isError && !validateInputNationality.isError &&
            !validateDateOfBorn.isError && !validateRadioSelectedCivilState.isError &&
            !validateRadioSelectedGender.isError && !validateListOfCheckBoxes.isError && !validateInputNumOfChildren.isError
    }
    fun checkInputNumOfChildren(){
        try {
            if (inputNumOfChildren.toInt() < 0 || inputNumOfChildren.toInt() > 50){
                validateInputNumOfChildren.isError = true
                validateInputNumOfChildren.errorMessage = "Numarul de copii este incorect"
            } else{
                validateInputNumOfChildren.isError = false
                validateInputNumOfChildren.errorMessage = ""
            }
            shouldEnabledSubmitButton()
        }catch (e:NumberFormatException){
            validateInputNumOfChildren.isError = true
            validateInputNumOfChildren.errorMessage = "Indicați numărul de copii"
            Log.d(TAG, "Error : $e")
        }
    }
    fun checkInputName(){
        if (!inputName.matches("^[A-Za-zțșăîâ]{2,25}(-[A-Za-zțșăîâ]{2,25}){0,4}$".toRegex())){
            validateInputName.isError = true
            validateInputName.errorMessage = "Numele este prea scurt (lung) sau ati utilizat simboluri interzise($%#@, simboluri din limba rusa)"
        }else{
            validateInputName.isError = false
            validateInputName.errorMessage = ""
        }

        shouldEnabledSubmitButton()
    }
    fun checkLastName(){
        if (!inputLastName.matches("^[A-Za-zțșăîâ]{2,25}(-[A-Za-zțșăîâ]{2,25}){0,4}$".toRegex())){
            validateLastName.isError = true
            validateLastName.errorMessage = "Prenumele este prea scurt (lung) sau ati utilizat simboluri interzise(\$%#@, simboluri din limba rusa)"
        }else{
            validateLastName.isError = false
            validateLastName.errorMessage = ""
        }
        shouldEnabledSubmitButton()
    }

    fun checkGenderRadio(){
        if (radioSelectedGender.isEmpty()){
            validateRadioSelectedGender.isError = true
            validateRadioSelectedGender.errorMessage = "Nu este selectată nici o opțiune"
        }else{
            validateRadioSelectedGender.isError = false
            validateRadioSelectedGender.errorMessage = ""
        }
        shouldEnabledSubmitButton()
    }
    fun checkDateOfBorn(){
        if(dateOfBorn.isEmpty()){
            validateDateOfBorn.isError = true
            validateDateOfBorn.errorMessage = "Selectați data de naștere"
        }else{
            validateDateOfBorn.isError = false
            validateDateOfBorn.errorMessage = ""
        }
        shouldEnabledSubmitButton()
    }
    fun checkCivilStateRadio(){
        if (radioSelectedCivilState.isEmpty()){
            validateRadioSelectedCivilState.isError = true
            validateRadioSelectedCivilState.errorMessage = "Nu este selectată nici o opțiune"
        }else{
            validateRadioSelectedCivilState.isError = false
            validateRadioSelectedCivilState.errorMessage = ""
        }
        shouldEnabledSubmitButton()
    }
    fun checkInputNationality(){
        if (!inputNationality.matches("^[A-Za-zșțăîâ]{3,30}$".toRegex())){
            validateInputNationality.isError = true
            validateInputNationality.errorMessage = "Minim 3 simboluri, maxim 30. Simboluri permise A-Z a-z (inclusiv diacritice)"
        }else{
            validateInputNationality.isError = false
            validateInputNationality.errorMessage = ""
        }
        shouldEnabledSubmitButton()
    }

    fun checkInputLanguage(){
        if (!inputLanguage.matches("^[A-Za-zșțăîâ]{3,30}$".toRegex())){
            validateInputLanguage.isError = true
            validateInputLanguage.errorMessage = "Minim 3 simboluri, maxim 30. Simboluri permise A-Z a-z (inclusiv diacritice)"
        }else{
            validateInputLanguage.isError = false
            validateInputLanguage.errorMessage = ""
        }
        shouldEnabledSubmitButton()
    }

    fun checkCheckboxes(){
        validateListOfCheckBoxes.isError = true
        validateListOfCheckBoxes.errorMessage = "Selectați minim o opțiune"
        for (elem in listOfCheckBoxes){
            if (elem.checkBoxValue){
                validateListOfCheckBoxes.isError = false
                validateListOfCheckBoxes.errorMessage = ""
                break;
            }
        }
        shouldEnabledSubmitButton()
    }

    fun postAnswer(){
        questionnaireUiState = QuestionnaireUiState.Loading(true)
        val answer = CensusQuestionnaire(
            name = inputName,
            lastName = inputLastName,
            gender = radioSelectedGender,
            civilState = radioSelectedCivilState,
            numOfChildren = inputNumOfChildren.toInt(),
            nationality = inputNationality,
            bornDate = dateOfBorn,
            nativeLanguage = inputLanguage,
            income = listOfCheckBoxes
        )
        viewModelScope.launch {
            try {
                censusRepository.postAnswerQuestionnaire(answer)
                questionnaireUiState = QuestionnaireUiState.Success(true)
                delay(2000)
                questionnaireUiState = QuestionnaireUiState.Success(false)
            }catch (e: IOException){
                Log.d(TAG, "$e")
                questionnaireUiState =
                    QuestionnaireUiState.Error(true, errorMessage = "Input/Output error")
            }catch (e: HttpException){
                Log.d(TAG, "$e")
                questionnaireUiState =
                    QuestionnaireUiState.Error(true, "Server-ul nu lucrează. Mai încercați")
            }
            catch (e: SocketTimeoutException){
                Log.e(TAG, "$e")
                questionnaireUiState = QuestionnaireUiState.Error(
                    true,
                    errorMessage = "A experat timpul de așteptare. Mai încercați"
                )

            }
        }
    }

    fun updateInputName(name : String){
        inputName = name.trim()
    }
    fun updateInputNumOfChildren(num : String){
            inputNumOfChildren = num.trim()
    }
    fun updateInputLastName(lastName : String){
        inputLastName = lastName.trim()
    }
    fun updateInputNationality(nationality : String){
        inputNationality = nationality.trim()
    }
    fun updateInputLanguage(language : String){
        inputLanguage = language.trim()
    }

    fun radioUpdateSelectedGender(gender : String){
        radioSelectedGender = gender.trim()
    }

    fun radioUpdateSelectedCivilState(civilState : String){
        radioSelectedCivilState = civilState
    }

    fun updateDateOfBorn(date : String){
        dateOfBorn = date
    }

    fun updateStatesOfCheckBox(index : Int, checked: Boolean){
        _listOfCheckBoxes[index] = _listOfCheckBoxes[index].copy(checkBoxValue = checked)
    }

    fun closeErrorDialog(){
        questionnaireUiState = QuestionnaireUiState.Error(false)
    }

    companion object{
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =(this[APPLICATION_KEY] as CensusApplication)
                val censusRepository = application.container.censusRepository
                QuestionnaireViewModel(censusRepository = censusRepository)

            }
        }
    }
}