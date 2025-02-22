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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.zen.vlrnotifications.helpers.NotificationScheduler
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.zen.vlrnotifications.models.ValorantMatchModel
import com.zen.vlrnotifications.network.Resource
import com.zen.vlrnotifications.ui.theme.ValorantMatchNotificationsTheme
import com.zen.vlrnotifications.viewmodel.MainViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

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
    val helper = SharedPreferenceHelper(context)
    val matchesResponse by mainViewModel.vlrMatchesMutableStatFlow.collectAsState()
    var matchList = remember { mutableListOf<ValorantMatchModel>() }
    val cache = helper.getString("matches")
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (BuildConfig.DEBUG) {
        if (cache.isEmpty()) {
            LaunchedEffect(Unit) {
                mainViewModel.getMatches(1)
            }
        } else {
            matchList =
                Gson().fromJson(cache, Array<ValorantMatchModel>::class.java).toMutableList()
        }
    } else {
        LaunchedEffect(Unit) {
            mainViewModel.getMatches(1)
        }
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
                helper.putString("matches", Gson().toJson(matchList))
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
            LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
                items(matchList) { match ->
                    MatchCard(valorantMatchModel = match)
                }
            }
        }
    }
}

@Preview
@Composable
fun MatchCard(
    modifier: Modifier = Modifier,
    valorantMatchModel: ValorantMatchModel = ValorantMatchModel(
        date = "Sat, February 22, 2025",
        time = "4:00 AM",
        status = "Upcoming",
        event = "Champions Tour 2025: Masters Bangkok",
        series = "Swiss Stage–Round 2 (1-0)",
        team1 = "DRX",
        team2 = "Team Vitality",
        href = "/449000/drx-vs-team-vitality-champions-tour-2025-masters-bangkok-r2-1-0"
    )
) {
    val context = LocalContext.current
    Card(
        modifier = modifier.padding(bottom = 10.dp),
        shape = RoundedCornerShape(5.dp),
        onClick = {
            NotificationScheduler.setReminder(context, valorantMatchModel)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(
                formatDateTime(valorantMatchModel.getISTTime()),
//                "Today at 3:30 PM",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 20.sp),
            )
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valorantMatchModel.team1,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 18.sp)
                )
                Text(
                    text = "vs",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Text(
                    text = valorantMatchModel.team2,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 18.sp)
                )
            }
            Text(
                valorantMatchModel.series,
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.CenterHorizontally),
            )
            Text(
                valorantMatchModel.event,
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally),
            )
        }
    }
}


fun formatDateTime(input: String): String {
    val formatter = DateTimeFormatter.ofPattern("EEE, MMMM d, yyyy h:mm a", Locale.getDefault())
    val dateTime = LocalDateTime.parse(input, formatter)
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)

    return when (dateTime.toLocalDate()) {
        today -> "Today at ${
            dateTime.format(
                DateTimeFormatter.ofPattern(
                    "h:mm a",
                    Locale.getDefault()
                )
            )
        }"

        tomorrow -> "Tomorrow at ${
            dateTime.format(
                DateTimeFormatter.ofPattern(
                    "h:mm a",
                    Locale.getDefault()
                )
            )
        }"

        else -> input
    }
}