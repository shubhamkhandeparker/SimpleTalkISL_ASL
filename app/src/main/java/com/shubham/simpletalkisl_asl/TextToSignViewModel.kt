package com.shubham.simpletalkisl_asl

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * This is the 'Brain' (ViewModel) for our TextToSign Screen.
 * It holds the state and handles all the logics.
 * It extends :ViewModel() so the system knows to keep it alive.
 */

class TextToSignViewModel : ViewModel (){

    /**
     * This is a 'data class' that represent all the data.
     * our UI needs to show .We call this the 'state'.
     *
     * -textInput :What the user has typed.
     * - signDescription : The text explaining the sign(e.g,"A closed First ...") .
     */

    data class TextToSignUiState(
        val textInput :String="",
        val signDescription : String=""

        //We will add the image later to keep it simple
    )

    //This is the *Private* State .Only the viewModel can change it .
    //We create it with a default ,empty state.
    private val _uiState = MutableStateFlow(TextToSignUiState())

    //This is the *Public* State.The UI will "read" or listen to this .
    //It's read-only so the UI can't change it by accident.
    val uiState:StateFlow<TextToSignUiState> = _uiState.asStateFlow()

    /**
     * This function is called by the UI *every time * the user
     * types a new letter in the text box.
     */

    fun onTextChanged(newText: String){
        //We update the State with the new text
        _uiState.update { currentState ->
            currentState.copy(
                textInput =newText
            )
        }
    }
    /**
     * This function is called by the UI when the user
     * taps the "Translate" button
     */


}