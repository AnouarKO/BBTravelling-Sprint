package com.example.bbtraveling.ui.viewmodel

import com.example.bbtraveling.domain.ActivityDraft
import com.example.bbtraveling.domain.OperationResult
import com.example.bbtraveling.domain.Trip
import com.example.bbtraveling.domain.TripDraft
import com.example.bbtraveling.domain.TripStatus
import com.example.bbtraveling.domain.repository.TripRepository
import com.example.bbtraveling.domain.validation.TravelValidator
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TripsViewModelTest {

    private lateinit var repository: FakeTripRepository
    private lateinit var viewModel: TripsViewModel

    @Before
    fun setup() {
        repository = FakeTripRepository()
        viewModel = TripsViewModel(repository)
    }

    @Test
    fun addTrip_withInvalidDraftReturnsUiValidationErrorsWithoutCallingRepository() {
        val result = viewModel.addTrip(
            TripDraft(
                title = "",
                description = "",
                city = "",
                country = "",
                startDate = null,
                endDate = null,
                status = TripStatus.Planning,
                budgetEur = null
            )
        )

        assertTrue(result is OperationResult.Failure)
        result as OperationResult.Failure
        assertEquals(0, repository.addTripCalls)
        assertEquals(TravelValidator.ERROR_TITLE_REQUIRED, result.fieldErrors[TravelValidator.FIELD_TITLE])
        assertEquals(TravelValidator.ERROR_BUDGET_REQUIRED, result.fieldErrors[TravelValidator.FIELD_BUDGET])
    }

    @Test
    fun addTrip_withValidDraftDelegatesToRepository() {
        repository.addTripResult = OperationResult.Success

        val result = viewModel.addTrip(validTripDraft())

        assertTrue(result is OperationResult.Success)
        assertEquals(1, repository.addTripCalls)
    }

    @Test
    fun addActivity_withoutExistingTripReturnsTripNotFound() {
        val result = viewModel.addActivity(
            tripId = "missing",
            draft = ActivityDraft(
                title = "Museum",
                description = "Visit",
                date = LocalDate.now().plusMonths(2),
                time = java.time.LocalTime.of(10, 0),
                category = com.example.bbtraveling.domain.ActivityCategory.Museum,
                costEur = 20.0
            )
        )

        assertTrue(result is OperationResult.Failure)
        result as OperationResult.Failure
        assertEquals(TravelValidator.ERROR_TRIP_NOT_FOUND, result.message)
    }

    private fun validTripDraft(): TripDraft {
        val startDate = LocalDate.now().plusMonths(2)
        return TripDraft(
            title = "Lisbon",
            description = "Trip description",
            city = "Lisbon",
            country = "Portugal",
            startDate = startDate,
            endDate = startDate.plusDays(2),
            status = TripStatus.Planning,
            budgetEur = 300.0
        )
    }

    private class FakeTripRepository : TripRepository {
        override val trips: StateFlow<List<Trip>> = MutableStateFlow(emptyList())

        var addTripResult: OperationResult = OperationResult.Success
        var addTripCalls = 0

        override fun getTripById(tripId: String): Trip? = trips.value.firstOrNull { it.id == tripId }

        override fun addTrip(draft: TripDraft): OperationResult {
            addTripCalls += 1
            return addTripResult
        }

        override fun editTrip(
            tripId: String,
            draft: TripDraft,
            moveActivitiesWithTrip: Boolean
        ): OperationResult = OperationResult.Success

        override fun deleteTrip(tripId: String): OperationResult = OperationResult.Success

        override fun addActivity(tripId: String, draft: ActivityDraft): OperationResult = OperationResult.Success

        override fun updateActivity(
            tripId: String,
            activityId: String,
            draft: ActivityDraft
        ): OperationResult = OperationResult.Success

        override fun deleteActivity(tripId: String, activityId: String): OperationResult = OperationResult.Success
    }
}
