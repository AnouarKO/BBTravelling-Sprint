package com.example.bbtraveling.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bbtraveling.data.MockData
import com.example.bbtraveling.domain.Trip

private enum class TripFilter(val label: String) {
    All("All"),
    Upcoming("Upcoming"),
    Planning("Planning"),
    Other("Other")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    trips: List<Trip> = MockData.trips,
    onTripClick: (String) -> Unit
) {
    var selectedFilter by remember { mutableIntStateOf(0) }
    val filters = TripFilter.entries
    val filteredTrips = trips.filterBy(filters[selectedFilter])

    Scaffold(
        topBar = { TopAppBar(title = { Text("Trips") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedFilter) {
                filters.forEachIndexed { index, filter ->
                    Tab(
                        selected = selectedFilter == index,
                        onClick = { selectedFilter = index },
                        text = { Text(filter.label) }
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SummaryCard(total = trips.size, shown = filteredTrips.size, filter = filters[selectedFilter].label)
                }

                itemsIndexed(filteredTrips) { index, trip ->
                    TripRowCard(
                        trip = trip,
                        index = index,
                        onClick = { onTripClick(trip.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(total: Int, shown: Int, filter: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Trip overview", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Showing $shown of $total trips ($filter)",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TripRowCard(trip: Trip, index: Int, onClick: () -> Unit) {
    val containerColor = if (index % 2 == 0) {
        Color(0xFF6A1CF7).copy(alpha = 0.12f)
    } else {
        Color(0xFFFFC928).copy(alpha = 0.20f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = trip.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(text = trip.destination)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(trip.status, style = MaterialTheme.typography.labelLarge)
                Text("${trip.startDate} - ${trip.endDate}", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Budget EUR ${trip.budgetEur.toInt()} | Spent EUR ${"%.0f".format(trip.spentEur)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun List<Trip>.filterBy(filter: TripFilter): List<Trip> {
    return when (filter) {
        TripFilter.All -> this
        TripFilter.Upcoming -> filter { it.status.contains("ready", true) || it.status.contains("booked", true) }
        TripFilter.Planning -> filter { it.status.contains("planning", true) }
        TripFilter.Other -> filterNot {
            it.status.contains("ready", true) ||
                it.status.contains("booked", true) ||
                it.status.contains("planning", true)
        }
    }
}
