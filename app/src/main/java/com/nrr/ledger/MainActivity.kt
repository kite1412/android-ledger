package com.nrr.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.nrr.ledger.presentation.MainPage
import com.nrr.ledger.presentation.UsernamePage
import com.nrr.ledger.realm.RealmApp
import com.nrr.ledger.ui.theme.LedgerTheme
import com.nrr.ledger.util.MAIN_PAGE
import com.nrr.ledger.util.USERNAME_PAGE
import com.nrr.ledger.util.USERNAME_SP

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController= rememberNavController()
            LedgerTheme {
                NavHost(
                    navController = navController,
                    graph = navController.createGraph(startDestination = MAIN_PAGE) {
                        composable(MAIN_PAGE) {
                            MainPage(navController = navController)
                        }
                        composable(USERNAME_PAGE) {
                            UsernamePage(navController = navController)
                        }
                    }
                )
            }
        }
    }
}