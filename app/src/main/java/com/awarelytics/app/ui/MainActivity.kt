package com.awarelytics.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.awarelytics.app.ui.navigation.AwarelyticsNavigation
import com.awarelytics.app.ui.navigation.Routes
import com.awarelytics.app.ui.theme.AwarelyticsTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AwarelyticsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Start at dashboard if already logged in, else login screen
                    val startDest = if (FirebaseAuth.getInstance().currentUser != null) {
                        Routes.DASHBOARD
                    } else {
                        Routes.LOGIN
                    }

                    AwarelyticsNavigation(
                        navController = navController,
                        startDestination = startDest
                    )
                }
            }
        }
    }
}
