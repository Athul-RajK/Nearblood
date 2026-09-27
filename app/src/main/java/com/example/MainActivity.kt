package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.navigation.MainAppNavigation
import com.example.ui.theme.NearbloodTheme
import com.example.ui.viewmodel.BloodViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class MainActivity : ComponentActivity() {
  private val viewModel: BloodViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    try {
      if (FirebaseApp.getApps(this).isEmpty()) {
        val options = FirebaseOptions.Builder()
          .setApiKey("AIzaSyDHDl5mkY3-m1YVK4lYcebNzXiX5Y6pSx4")
          .setApplicationId("1:683140744962:web:1fcd7b486e4e015d2fe41b")
          .setProjectId("athulmusic-b6ea1")
          .setDatabaseUrl("https://athulmusic-b6ea1-default-rtdb.firebaseio.com")
          .setGcmSenderId("683140744962")
          .setStorageBucket("athulmusic-b6ea1.firebasestorage.app")
          .build()
        FirebaseApp.initializeApp(this, options)
      }
    } catch (e: Exception) {
      Log.e("MainActivity", "Firebase initialization exception: ${e.message}")
    }
    setContent {
      NearbloodTheme {
        MainAppNavigation(viewModel = viewModel)
      }
    }
  }
}

