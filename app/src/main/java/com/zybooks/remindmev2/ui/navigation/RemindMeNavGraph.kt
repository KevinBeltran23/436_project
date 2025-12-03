package com.zybooks.remindmev2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zybooks.remindmev2.ui.screens.AddEditReminderScreen
import com.zybooks.remindmev2.ui.screens.MapLocationPickerScreen
import com.zybooks.remindmev2.ui.screens.ReminderListScreen
import com.zybooks.remindmev2.ui.screens.SettingsScreen

object RemindMeDestinations {
    const val HOME_ROUTE = "home"
    const val ADD_EDIT_ROUTE = "add_edit_reminder"
    const val MAP_ROUTE = "map_location_picker"
    const val SETTINGS_ROUTE = "settings"
    const val REMINDER_ID_ARG = "reminderId"
}

@Composable
fun RemindMeNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = RemindMeDestinations.HOME_ROUTE,
        modifier = modifier
    ) {
        composable(RemindMeDestinations.HOME_ROUTE) {
            ReminderListScreen(
                onAddReminder = { navController.navigate(RemindMeDestinations.ADD_EDIT_ROUTE) },
                onReminderClick = { reminderId ->
                    navController.navigate("${RemindMeDestinations.ADD_EDIT_ROUTE}?${RemindMeDestinations.REMINDER_ID_ARG}=$reminderId")
                },
                onSettingsClick = { navController.navigate(RemindMeDestinations.SETTINGS_ROUTE) }
            )
        }
        
        composable(
            route = "${RemindMeDestinations.ADD_EDIT_ROUTE}?${RemindMeDestinations.REMINDER_ID_ARG}={${RemindMeDestinations.REMINDER_ID_ARG}}",
            arguments = listOf(
                androidx.navigation.navArgument(RemindMeDestinations.REMINDER_ID_ARG) {
                    type = androidx.navigation.NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getLong(RemindMeDestinations.REMINDER_ID_ARG) ?: -1L
            val locationResult = backStackEntry.savedStateHandle.get<Triple<Double, Double, String>>("location_data")
            
            if (locationResult != null) {
                backStackEntry.savedStateHandle.remove<Triple<Double, Double, String>>("location_data")
            }

            AddEditReminderScreen(
                reminderId = if (reminderId == -1L) null else reminderId,
                onNavigateUp = { navController.navigateUp() },
                onPickLocation = { 
                    navController.navigate(RemindMeDestinations.MAP_ROUTE) 
                },
                locationResult = locationResult
            )
        }

        composable(RemindMeDestinations.MAP_ROUTE) {
            MapLocationPickerScreen(
                onLocationSelected = { lat, lng, name ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("location_data", Triple(lat, lng, name))
                    navController.popBackStack()
                },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(RemindMeDestinations.SETTINGS_ROUTE) {
            SettingsScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}

