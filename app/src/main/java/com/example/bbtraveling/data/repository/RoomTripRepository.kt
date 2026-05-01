package com.example.bbtraveling.data.repository

import com.example.bbtraveling.data.MockData
import com.example.bbtraveling.data.local.dao.ItineraryItemDao
import com.example.bbtraveling.data.local.dao.TripDao
import com.example.bbtraveling.data.local.entity.ItineraryItemEntity
import com.example.bbtraveling.data.local.entity.TripEntity
import com.example.bbtraveling.data.local.model.TripWithActivities
import com.example.bbtraveling.domain.Activity
import com.example.bbtraveling.domain.ActivityDraft
import com.example.bbtraveling.domain.OperationResult
import com.example.bbtraveling.domain.Photo
import com.example.bbtraveling.domain.Trip
import com.example.bbtraveling.domain.TripDraft
import com.example.bbtraveling.domain.repository.TripRepository
import com.example.bbtraveling.domain.validation.TravelValidator
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Singleton
class RoomTripRepository @Inject constructor(
    private val tripDao: TripDao,
    private val itineraryItemDao: ItineraryItemDao,
    private val clock: Clock
) : TripRepository {

    private val ioDispatcher = Dispatchers.IO
    private val repositoryScope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val seedPhotosByTripId: Map<String, List<Photo>> =
        MockData.initialTrips().associate { trip -> trip.id to trip.photos }

    override val trips: StateFlow<List<Trip>> = tripDao.observeTripsWithActivities()
        .map { entities -> entities.map(::toDomainTrip) }
        .stateIn(repositoryScope, SharingStarted.Eagerly, emptyList())

    init {
        repositoryScope.launch {
            seedDatabaseIfNeeded()
        }
    }

    override fun getTripById(tripId: String): Trip? = trips.value.firstOrNull { it.id == tripId }

    override fun addTrip(draft: TripDraft): OperationResult {
        val errors = TravelValidator.validateTripDraft(
            draft = draft,
            today = today(),
            existingActivities = emptyList()
        )
        if (errors.isNotEmpty()) return OperationResult.Failure(fieldErrors = errors)

        val newTrip = TripEntity(
            id = UUID.randomUUID().toString(),
            ownerLogin = null,
            title = draft.title.trim(),
            description = draft.description.trim(),
            city = draft.city.trim(),
            country = draft.country.trim(),
            startDate = checkNotNull(draft.startDate),
            endDate = checkNotNull(draft.endDate),
            status = draft.status,
            accommodation = "",
            transport = "",
            travelers = 1,
            budgetEur = checkNotNull(draft.budgetEur),
            createdAt = LocalDateTime.now(clock)
        )

        runBlocking(ioDispatcher) {
            tripDao.upsertTrip(newTrip)
        }
        return OperationResult.Success
    }

    override fun editTrip(
        tripId: String,
        draft: TripDraft,
        moveActivitiesWithTrip: Boolean
    ): OperationResult {
        val currentTrip = getTripById(tripId)
            ?: return OperationResult.Failure(message = TravelValidator.ERROR_TRIP_NOT_FOUND)

        val activitiesForValidation = if (moveActivitiesWithTrip && draft.startDate != null) {
            currentTrip.rescheduleActivities(draft.startDate)
        } else {
            currentTrip.activities
        }

        val errors = TravelValidator.validateTripDraft(
            draft = draft,
            today = today(),
            existingActivities = activitiesForValidation
        )
        if (errors.isNotEmpty()) return OperationResult.Failure(fieldErrors = errors)

        val updatedTrip = TripEntity(
            id = currentTrip.id,
            ownerLogin = null,
            title = draft.title.trim(),
            description = draft.description.trim(),
            city = draft.city.trim(),
            country = draft.country.trim(),
            startDate = checkNotNull(draft.startDate),
            endDate = checkNotNull(draft.endDate),
            status = draft.status,
            accommodation = currentTrip.accommodation,
            transport = currentTrip.transport,
            travelers = currentTrip.travelers,
            budgetEur = checkNotNull(draft.budgetEur),
            createdAt = LocalDateTime.now(clock)
        )

        runBlocking(ioDispatcher) {
            tripDao.upsertTrip(updatedTrip)
            if (moveActivitiesWithTrip) {
                itineraryItemDao.upsertItems(
                    activitiesForValidation.map { activity -> activity.toEntity(tripId, clock) }
                )
            }
        }
        return OperationResult.Success
    }

    override fun deleteTrip(tripId: String): OperationResult {
        val currentTrip = getTripById(tripId)
            ?: return OperationResult.Failure(message = TravelValidator.ERROR_TRIP_NOT_FOUND)

        runBlocking(ioDispatcher) {
            tripDao.deleteTripById(currentTrip.id)
        }
        return OperationResult.Success
    }

    override fun addActivity(tripId: String, draft: ActivityDraft): OperationResult {
        val trip = getTripById(tripId)
            ?: return OperationResult.Failure(message = TravelValidator.ERROR_TRIP_NOT_FOUND)
        val errors = TravelValidator.validateActivityDraft(draft = draft, trip = trip, today = today())
        if (errors.isNotEmpty()) return OperationResult.Failure(fieldErrors = errors)

        val newItem = ItineraryItemEntity(
            id = UUID.randomUUID().toString(),
            tripId = tripId,
            title = draft.title.trim(),
            description = draft.description.trim(),
            date = checkNotNull(draft.date),
            time = checkNotNull(draft.time),
            category = draft.category,
            costEur = checkNotNull(draft.costEur),
            createdAt = LocalDateTime.now(clock)
        )

        runBlocking(ioDispatcher) {
            itineraryItemDao.upsertItem(newItem)
        }
        return OperationResult.Success
    }

    override fun updateActivity(tripId: String, activityId: String, draft: ActivityDraft): OperationResult {
        val trip = getTripById(tripId)
            ?: return OperationResult.Failure(message = TravelValidator.ERROR_TRIP_NOT_FOUND)
        val currentActivity = trip.activities.firstOrNull { it.id == activityId }
            ?: return OperationResult.Failure(message = TravelValidator.ERROR_ACTIVITY_NOT_FOUND)

        val errors = TravelValidator.validateActivityDraft(draft = draft, trip = trip, today = today())
        if (errors.isNotEmpty()) return OperationResult.Failure(fieldErrors = errors)

        val updatedItem = ItineraryItemEntity(
            id = currentActivity.id,
            tripId = tripId,
            title = draft.title.trim(),
            description = draft.description.trim(),
            date = checkNotNull(draft.date),
            time = checkNotNull(draft.time),
            category = draft.category,
            costEur = checkNotNull(draft.costEur),
            createdAt = LocalDateTime.now(clock)
        )

        runBlocking(ioDispatcher) {
            itineraryItemDao.upsertItem(updatedItem)
        }
        return OperationResult.Success
    }

    override fun deleteActivity(tripId: String, activityId: String): OperationResult {
        val trip = getTripById(tripId)
            ?: return OperationResult.Failure(message = TravelValidator.ERROR_TRIP_NOT_FOUND)
        if (trip.activities.none { it.id == activityId }) {
            return OperationResult.Failure(message = TravelValidator.ERROR_ACTIVITY_NOT_FOUND)
        }

        runBlocking(ioDispatcher) {
            itineraryItemDao.deleteItemById(activityId)
        }
        return OperationResult.Success
    }

    private suspend fun seedDatabaseIfNeeded() {
        if (tripDao.countTrips() > 0) return

        val initialTrips = MockData.initialTrips()
        tripDao.upsertTrips(initialTrips.map { it.toEntity(clock) })
        itineraryItemDao.upsertItems(
            initialTrips.flatMap { trip ->
                trip.activities.map { activity -> activity.toEntity(trip.id, clock) }
            }
        )
    }

    private fun toDomainTrip(source: TripWithActivities): Trip {
        return Trip(
            id = source.trip.id,
            title = source.trip.title,
            startDate = source.trip.startDate,
            endDate = source.trip.endDate,
            description = source.trip.description,
            destination = "${source.trip.city}, ${source.trip.country}",
            status = source.trip.status,
            accommodation = source.trip.accommodation,
            transport = source.trip.transport,
            travelers = source.trip.travelers,
            budgetEur = source.trip.budgetEur,
            activities = source.activities
                .map { item ->
                    Activity(
                        id = item.id,
                        title = item.title,
                        description = item.description,
                        date = item.date,
                        time = item.time,
                        category = item.category,
                        costEur = item.costEur
                    )
                }
                .sortedWith(compareBy<Activity>({ it.date }, { it.time })),
            photos = seedPhotosByTripId[source.trip.id].orEmpty()
        )
    }

    private fun Trip.toEntity(clock: Clock): TripEntity {
        val city = destination.substringBefore(",").trim()
        val country = destination.substringAfter(",", "").trim()
        return TripEntity(
            id = id,
            ownerLogin = null,
            title = title,
            description = description,
            city = city,
            country = country,
            startDate = startDate,
            endDate = endDate,
            status = status,
            accommodation = accommodation,
            transport = transport,
            travelers = travelers,
            budgetEur = budgetEur,
            createdAt = LocalDateTime.now(clock)
        )
    }

    private fun Activity.toEntity(tripId: String, clock: Clock): ItineraryItemEntity {
        return ItineraryItemEntity(
            id = id,
            tripId = tripId,
            title = title,
            description = description,
            date = date,
            time = time,
            category = category,
            costEur = costEur,
            createdAt = LocalDateTime.now(clock)
        )
    }

    private fun today(): LocalDate = LocalDate.now(clock)
}
