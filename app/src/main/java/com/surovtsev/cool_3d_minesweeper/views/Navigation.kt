package com.surovtsev.cool_3d_minesweeper.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.surovtsev.cool_3d_minesweeper.presentation.Screen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.MainScreen.route
    ) {
        composable(
            route = Screen.MainScreen.route
        ) {
            MainScreen(navController = navController)
        }
        composable(
            route = Screen.RankingScreen.route + "/{name}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = "Sasha"
                    nullable = true
                }
            )
        ) { entry ->
            RankingScreen(name = entry.arguments?.getString("name"))
        }
    }
}

@Composable
fun MainScreen(navController: NavController) {
    var text by remember {
        mutableStateOf("")
    }
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp)
    ) {
        TextField(
            value = text,
            onValueChange = {
                text = it
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            navController.navigate(
                Screen.RankingScreen.withArgs(text)
            )
        },
        modifier = Modifier
            .align(Alignment.End)) {
            Text(text = "To RankingScreen")
        }
    }
}

@Composable
fun RankingScreen(name: String?) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(text = "Hello, $name")
    }
}