package com.zen.vlrnotifications

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zen.vlrnotifications.models.ValorantMatchModel
import com.zen.vlrnotifications.network.Resource
import com.zen.vlrnotifications.ui.theme.ValorantMatchNotificationsTheme
import com.zen.vlrnotifications.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ValorantMatchNotificationsTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(mainViewModel: MainViewModel = MainViewModel()) {

    val matchesResponse = mainViewModel.vlrMatchesMutableStatFlow.collectAsState().value
    val matchList = remember { mutableListOf<ValorantMatchModel>() }

    LaunchedEffect(matchesResponse.status) {
        when (matchesResponse.status) {
            Resource.Status.SUCCESS -> {
                matchesResponse.data?.let {
                    matchList.addAll(it)
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

    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Button(
                onClick = {
                    mainViewModel.getMatches(1)
                }
            ) {
                Text("Get Matches")
            }
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(matchList) { match ->
                    MatchCard(match)
                }
            }
        }
    }
}

@Composable
fun MatchCard(valorantMatchModel: ValorantMatchModel) {
    Card(colors = CardDefaults.cardColors().copy(containerColor = Color.White)) {
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