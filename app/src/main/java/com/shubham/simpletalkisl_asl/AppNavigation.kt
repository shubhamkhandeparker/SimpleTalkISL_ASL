package com.shubham.simpletalkisl_asl

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.ui.graphics.vector.ImageVector


sealed class Screen (val route: String,val title: String, val icon: ImageVector){

    object TextToSign :Screen(
        route = "text_to_sign",
        title="Text",
        icon = Icons.Filled.TextFields
    )

    object SignToText:Screen(
        route = "sign_to_text",
        title = "camera",
        icon = Icons.Filled.Videocam
    )

}


val bottomNavItems = listOf(
    Screen.TextToSign,
    Screen.SignToText
)