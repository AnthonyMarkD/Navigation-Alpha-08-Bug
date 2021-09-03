package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.test.ui.theme.TestTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.CountDownLatch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestTheme {

                NavGraph()

            }
        }
    }
}

@Composable
fun NavGraph(startDestination: String = Screen.Screen1.route) {
    val navController = rememberNavController()
    //TODO Uncomment these two lines to reproduce crash.
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentDestination = navBackStackEntry?.destination
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {

        composable(Screen.Screen1.route) {
            Screen1(navigateTo2 = { navController.navigate(Screen.Screen2.route) })
        }
        navigation(
            startDestination = Screen.Screen2.route,
            route = "route"
        ) {
            composable(Screen.Screen2.route) {
                val baseViewModel = hiltViewModel<BaseViewModel>(
                    remember { navController.getBackStackEntry("route") }
                )
                Screen2(baseViewModel, navigateBack = { navController.popBackStack() })
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Screen1 : Screen("screen_1")
    object Screen2 : Screen("screen_2")

}

@Composable
fun Screen1(navigateTo2: () -> Unit) {
    Surface() {
        Column() {
            Text(text = "Hello 1")
            Spacer(modifier = Modifier.height(32.dp))
            TextButton(onClick = { navigateTo2() }) {
                Text("Nav to 2")
            }
        }
    }
}

@Composable
fun Screen2(baseViewModel: BaseViewModel, navigateBack: () -> Unit) {
    Surface() {
        Column() {
            Text(text = "Hello 2")
            Text(text = baseViewModel.count)
            Spacer(modifier = Modifier.height(32.dp))
            TextButton(onClick = { navigateBack() }) {
                Text("Nav to 2")
            }
        }
    }
}
