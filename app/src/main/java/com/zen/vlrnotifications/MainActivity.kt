package com.zen.vlrnotifications

import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.zen.vlrnotifications.helpers.NotificationScheduler
import com.zen.vlrnotifications.models.ValorantMatchModel
import com.zen.vlrnotifications.network.Resource
import com.zen.vlrnotifications.ui.theme.ValorantMatchNotificationsTheme
import com.zen.vlrnotifications.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue with the app's functionality.
        } else {
            // Permission is denied. Handle the case.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ValorantMatchNotificationsTheme {
                MainScreen()
            }
        }
        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted. Continue with the app's functionality.
            }

            shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                // Show an explanation to the user why the permission is needed.
            }

            else -> {
                // Directly request the permission.
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}


@Composable
fun MainScreen(mainViewModel: MainViewModel = MainViewModel()) {

    val context = LocalContext.current
    val matchesResponse by mainViewModel.vlrMatchesMutableStatFlow.collectAsState()
    val matchList = remember { mutableListOf<ValorantMatchModel>() }
    val notificationScheduler = NotificationScheduler(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    LaunchedEffect(Unit) {
        mainViewModel.getMatches(1)
    }

    LaunchedEffect(matchesResponse.status) {
        when (matchesResponse.status) {
            Resource.Status.SUCCESS -> {
                matchesResponse.data?.let {
                    matchList.addAll(it)
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            matchList.forEach { match ->
                                notificationScheduler.scheduleNotification(match)
                            }
                        } else {
                            val intent = Intent().apply {
                                action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                            }
                            context.startActivity(intent)
                        }
                    }*/
                }
                // Handle success
            }

            Resource.Status.ERROR -> {
                // Handle error
            }

            Resource.Status.LOADING -> {
                // Handle loading
            }

            Resource.Status.IDLE -> {
                // Handle idle
            }
        }
    }

    Scaffold(contentColor = Color.White) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(matchList) { match ->
                    Log.d("TAG", "MainScreen: ")
                    MatchCard(match)
                }
            }
        }
    }
}

@Composable
fun MatchCard(valorantMatchModel: ValorantMatchModel) {
    Card(colors = CardDefaults.cardColors().copy(containerColor = Color.LightGray)) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valorantMatchModel.team1,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "vs",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Text(
                    text = valorantMatchModel.team2,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Text(valorantMatchModel.event, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${valorantMatchModel.date} ${valorantMatchModel.time}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}