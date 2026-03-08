package com.example.bbtraveling.domain

data class Trip(
    val id: String,
    val title: String,
    val destination: String,
    val status: String,
    val startDate: String,
    val endDate: String,
    val summary: String,
    val accommodation: String,
    val transport: String,
    val travelers: Int,
    val budgetEur: Double,
    val activities: List<Activity>,
    val photos: List<Photo>
) {
    val spentEur: Double get() = activities.sumOf { it.costEur }
    val remainingEur: Double get() = budgetEur - spentEur

    fun isOverBudget(): Boolean = spentEur > budgetEur

    fun averageActivityCost(): Double = if (activities.isEmpty()) 0.0 else spentEur / activities.size

    fun projectedDailyBudget(totalDays: Int): Double = if (totalDays <= 0) 0.0 else budgetEur / totalDays
}
