package com.shubham.simpletalkisl_asl

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.to
import com.shubham.simpletalkisl_asl.R

/**
 * This is the 'Brain' (ViewModel) for our TextToSign Screen.
 * It holds the state and handles all the logics.
 * It extends :ViewModel() so the system knows to keep it alive.
 */

data class SignTranslation(
    val imageResId: Int,
    val description: String
)

class TextToSignViewModel : ViewModel() {

    /**
     * This is a 'data class' that represent all the data.
     * our UI needs to show .We call this the 'state'.
     *
     * -textInput :What the user has typed.
     * - signDescription : The text explaining the sign(e.g,"A closed First ...") .
     */

    data class TextToSignUiState(
        val textInput: String = "",
        val translation: List<SignTranslation> = emptyList(),
        val errorMessage: String? = null

    )

    //This is the *Private* State .Only the viewModel can change it .
    //We create it with a default ,empty state.
    private val _uiState = MutableStateFlow(TextToSignUiState())

    //This is the *Public* State.The UI will "read" or listen to this .
    //It's read-only so the UI can't change it by accident.
    val uiState: StateFlow<TextToSignUiState> = _uiState.asStateFlow()

    /**
     * This function is called by the UI *every time * the user
     * types a new letter in the text box.
     */

    fun onTextChanged(newText: String) {
        //We update the State with the new text
        _uiState.update { currentState ->
            currentState.copy(
                textInput = newText
            )
        }
    }

    /**
     * This function is called by the UI when the user
     * taps the "Translate" button
     */

    fun onTranslateClicked() {
        //Get the current text from the state
        //Make it lowercase, and remote any extra spaces.
        val textToTranslate = _uiState.value.textInput.trim().lowercase()

        //---This is our "mock" database for now---
        //It's a simple 'map' that links text String to its description
        val signDatabase = mapOf(
            "a" to Pair(R.drawable.a_sign, "A closed fist with the thumb on the side"),
            "b" to Pair(R.drawable.b_sign, "An open palm with four fingers up , thumb tucked in"),
            "c" to Pair(R.drawable.c_sign, "A 'c' shape with your hand."),
            "d" to Pair(R.drawable.d_sign, "Index finger pointing up other fingers in a circle"),
            "e" to Pair(R.drawable.e_sign, "Fingers curled in , thumb tucked"),
            "f" to Pair(
                R.drawable.f_sign,
                "Thumb and index finger form a circle, other fingers spread up."
            ),
            "g" to Pair(
                R.drawable.g_sign,
                "Index finger and thumb extended parallel, palm facing side."
            ),
            "h" to Pair(R.drawable.h_sign, "Index and middle fingers together, pointing forward."),
            "i" to Pair(
                R.drawable.i_sign,
                "Little finger up, other fingers folded, thumb across palm."
            ),
            "j" to Pair(R.drawable.j_sign, "Draw a 'J' in the air with your little finger."),
            "k" to Pair(
                R.drawable.k_sign,
                "Index and middle fingers spread like a 'V', thumb touching the middle."
            ),
            "l" to Pair(R.drawable.l_sign, "Index finger up and thumb out, forming an 'L' shape."),
            "m" to Pair(
                R.drawable.m_sign,
                "Thumb tucked under three fingers, little finger extended."
            ),
            "n" to Pair(R.drawable.n_sign, "Thumb tucked under two fingers, other fingers curled."),
            "o" to Pair(
                R.drawable.o_sign,
                "All fingers curved to touch thumb, forming an 'O' shape."
            ),
            "p" to Pair(R.drawable.p_sign, "Like the 'K' sign but pointed downward."),
            "q" to Pair(R.drawable.q_sign, "Like the 'G' sign but pointed downward."),
            "r" to Pair(
                R.drawable.r_sign,
                "Index and middle fingers crossed, other fingers curled."
            ),
            "s" to Pair(R.drawable.s_sign, "A closed fist with the thumb in front of the fingers."),
            "t" to Pair(R.drawable.t_sign, "Thumb tucked between index and middle fingers."),
            "u" to Pair(R.drawable.u_sign, "Index and middle fingers together, pointing up."),
            "v" to Pair(R.drawable.v_sign, "Index and middle fingers spread apart forming a 'V'."),
            "w" to Pair(R.drawable.w_sign, "Three middle fingers up, forming a 'W'."),
            "x" to Pair(R.drawable.x_sign, "Index finger bent like a hook, other fingers curled."),
            "y" to Pair(
                R.drawable.y_sign,
                "Thumb and little finger extended, other fingers folded."
            ),
            "z" to Pair(R.drawable.z_sign, "Draw a 'Z' in the air with your index finger.")
        )

        //-------------------------------------------------------------------------------------------
        val resultList = mutableListOf<SignTranslation>()
        var founInvalidChar = false //A flag to track error

        for (char in textToTranslate) {
            //If the character is a space , just skip it
            if (char == ' ') {
                continue
            }

            val result = signDatabase[char.toString()]

            if (result != null) {
                //found it! Add it to our result list
                resultList.add(SignTranslation(result.first, result.second))
            } else {
                //Didn't find this character (like '?' or '1' )
                founInvalidChar = true
            }
        }

        if (resultList.isEmpty() && textToTranslate.isNotEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    translation = emptyList(),
                    errorMessage = "Sorry no valid sign found for that input",
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    translation = resultList,
                    errorMessage = if (founInvalidChar) "Some characters were skipped" else null
                )
            }
        }

    }
}