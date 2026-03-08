package com.example.bbtraveling.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TripTest {

    private val trip = Trip(
        id = "trip-test",
        title = "Test Trip",
        destination = "Test City",
        status = "Planning",
        startDate = "01 Jan 2026",
        endDate = "03 Jan 2026",
        summary = "Summary",
        accommodation = "Hotel",
        transport = "Flight",
        travelers = 2,
        budgetEur = 100.0,
        activities = listOf(
            Activity("10:00", "Museum", "Center", 20.0),
            Activity("14:00", "Lunch", "Old Town", 15.0)
        ),
        photos = emptyList()
    )

    @Test
    fun spentEur_isCalculatedFromActivities() {
        assertEquals(35.0, trip.spentEur, 0.001)
    }

    @Test
    fun remainingEur_isBudgetMinusSpent() {
        assertEquals(65.0, trip.remainingEur, 0.001)
    }

    @Test
    fun isOverBudget_returnsFalseWhenSpentBelowBudget() {
        assertFalse(trip.isOverBudget())
    }

    @Test
    fun averageActivityCost_returnsExpectedValue() {
        assertEquals(17.5, trip.averageActivityCost(), 0.001)
    }

    @Test
    fun projectedDailyBudget_handlesPositiveAndZeroDays() {
        assertEquals(50.0, trip.projectedDailyBudget(totalDays = 2), 0.001)
        assertEquals(0.0, trip.projectedDailyBudget(totalDays = 0), 0.001)
    }

    @Test
    fun isOverBudget_returnsTrueWhenSpentExceedsBudget() {
        val overBudgetTrip = trip.copy(
            budgetEur = 10.0
        )
        assertTrue(overBudgetTrip.isOverBudget())
    }
}
