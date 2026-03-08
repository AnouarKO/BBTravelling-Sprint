package com.example.bbtraveling.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bbtraveling.data.MockData
import com.example.bbtraveling.domain.Activity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripId: String,
    onBack: () -> Unit,
    onOpenGallery: () -> Unit
) {
    val trip = MockData.tripById(tripId)
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val scheme = MaterialTheme.colorScheme
    val headerGradient = listOf(Color(0xFF6117F3), Color(0xFFFFCA28))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trip?.title ?: "Trip") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (trip == null) {
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("Trip not found.")
            }
            return@Scaffold
        }

        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                listOf("Overview", "Itinerary").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            if (selectedTab == 0) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = scheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(headerGradient)
                                    )
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = trip.destination,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = trip.status,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "${trip.startDate} - ${trip.endDate}",
                                    color = Color.White.copy(alpha = 0.92f)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = trip.summary,
                                    color = Color.White.copy(alpha = 0.96f)
                                )
                            }
                        }
                    }

                    item {
                        Row {
                            StatCard(
                                label = "Budget",
                                value = "EUR ${trip.budgetEur.toInt()}",
                                icon = { Icon(Icons.Rounded.AttachMoney, contentDescription = null) },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(12.dp))
                            StatCard(
                                label = "Remaining",
                                value = "EUR ${"%.0f".format(trip.remainingEur)}",
                                icon = { Icon(Icons.Rounded.Schedule, contentDescription = null) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row {
                            StatCard(
                                label = "Travelers",
                                value = trip.travelers.toString(),
                                icon = { Icon(Icons.Rounded.Group, contentDescription = null) },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(12.dp))
                            StatCard(
                                label = "Activities",
                                value = trip.activities.size.toString(),
                                icon = { Icon(Icons.Rounded.Collections, contentDescription = null) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        DetailCard(
                            title = "Stay",
                            subtitle = trip.accommodation,
                            icon = { Icon(Icons.Rounded.Hotel, contentDescription = null) }
                        )
                    }

                    item {
                        DetailCard(
                            title = "Transport",
                            subtitle = trip.transport,
                            icon = { Icon(Icons.Rounded.Flight, contentDescription = null) }
                        )
                    }

                    item {
                        Button(onClick = onOpenGallery, modifier = Modifier.fillMaxWidth()) {
                            Text("Open trip gallery")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(trip.activities) { index, activity ->
                        ActivityRow(index = index + 1, activity = activity)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            icon()
            Spacer(Modifier.height(10.dp))
            Text(value, style = MaterialTheme.typography.titleLarge)
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(18.dp)) {
            BoxIcon(icon = icon)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun BoxIcon(icon: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
private fun ActivityRow(index: Int, activity: Activity) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(index.toString(), style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${activity.time} - ${activity.title}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(text = activity.location, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = "EUR ${"%.0f".format(activity.costEur)}", style = MaterialTheme.typography.labelLarge)
        }
    }
}
