package com.example.avance_proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.avance.MyApp
import com.example.avance.ui.theme.AvanceTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AvanceTheme {
                MyApp()
            }
        }
    }
}




