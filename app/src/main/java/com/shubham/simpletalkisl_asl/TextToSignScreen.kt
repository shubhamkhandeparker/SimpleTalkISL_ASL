package com.shubham.simpletalkisl_asl


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shubham.simpletalkisl_asl.ui.theme.SimpleTalkISLASLTheme
import kotlin.math.sin
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.width



/**
 * This is our "View"(the composable screen).
 * This is a "Smart" composable because it knows about the ViewModel.
 */


@Composable
fun TextToSignScreen(
    //This function get the viewModel instance we create earlier
    viewModel: TextToSignViewModel = viewModel()
) {
    //This is the magic line .
    //we "collect" the state from the viewModel.
    //'by' automatically unwraps it.
    //Whenever the data in the ViewModel changes , this UI will
    //automatically update itself (recompose ) .

    val uiState by viewModel.uiState.collectAsState()

    //We pass the current state down to the layout
    //and we pass the event (the functions) up the viewModel
    TextToSignLayout(
        uiState = uiState,
        onTextChanged = { newText -> viewModel.onTextChanged(newText) },
        onTranslateClicked = { viewModel.onTranslateClicked() }
    )
}

/**
 * This is the "dumb" layout .It just displays what it's given .
 * It doesn't know *What* to do , only *how* to show things
 * This makes it easy to preview and test
 */

@Composable
fun TextToSignLayout(
    uiState: TextToSignViewModel.TextToSignUiState,
    onTextChanged: (String) -> Unit,
    onTranslateClicked: () -> Unit
) {
    //Column arranges items Vertically

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    )
    {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                //A Simple Title
                Text(
                    text = "Enter text to translate ",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )


                OutlinedTextField(
                    value = uiState.textInput,  //The text to show (from the state)
                    onValueChange = onTextChanged, //The function to call when text changes
                    placeholder = { Text("Typing here...") },
                    modifier = Modifier.fillMaxWidth(),//make it fill the width
                    singleLine = false,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                //The translate Button
                Button(
                    onClick = onTranslateClicked, //The function to call when clicked
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("TRANSLATE TO SIGNS")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Text(
                    text = "Sign Language Guide",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                if (uiState.translation.isNotEmpty()) {

                    LazyRow(modifier = Modifier.fillMaxWidth()) {
                        items(uiState.translation) { sign ->
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .width(100.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = sign.imageResId),
                                    contentDescription = sign.description,
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(100.dp)
                                        .padding(bottom = 4.dp)
                                )
                                Text(
                                    text = sign.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else if (uiState.errorMessage !=null){
                    Text(
                        text=uiState.errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign= TextAlign.Center,
                        modifier =Modifier.fillMaxWidth()
                    )

                }
                else {
                    Text(
                        text = "Visual guide for each word will appear here",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun TextToSignScreenPreview() {
    SimpleTalkISLASLTheme {
        TextToSignLayout(
            uiState = TextToSignViewModel.TextToSignUiState(
                textInput = "Example",
              translation = emptyList(),
                errorMessage = null
            ),
            onTextChanged = {},  //In a preview ,event do nothing
            onTranslateClicked = {} //In a preview , event do nothing
        )
    }
}
