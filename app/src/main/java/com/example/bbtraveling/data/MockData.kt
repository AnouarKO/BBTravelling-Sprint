package com.example.bbtraveling.data

import com.example.bbtraveling.domain.Activity
import com.example.bbtraveling.domain.Photo
import com.example.bbtraveling.domain.Trip

object MockData {

    val trips: List<Trip> = listOf(
        Trip(
            id = "t1",
            title = "Weekend in Rome",
            destination = "Rome, Italy",
            status = "Ready to go",
            startDate = "14 Mar 2026",
            endDate = "16 Mar 2026",
            summary = "A compact cultural escape with landmarks, food and a sunset walk.",
            accommodation = "Trastevere Urban Suites",
            transport = "Direct flight from Barcelona",
            travelers = 2,
            budgetEur = 420.0,
            activities = listOf(
                Activity("09:00", "Colosseum entry", "Historic Center", 18.0),
                Activity("13:30", "Trastevere lunch", "Via della Scala", 22.5),
                Activity("19:45", "Gelato and evening walk", "Piazza Navona", 6.0)
            ),
            photos = listOf(
                Photo("p1", "Arrival day", "Fiumicino transfer", android.R.drawable.ic_menu_myplaces),
                Photo("p2", "Forum route", "Ancient Rome", android.R.drawable.ic_menu_compass),
                Photo("p3", "Dinner stop", "Trastevere", android.R.drawable.ic_menu_camera)
            )
        ),
        Trip(
            id = "t2",
            title = "Barcelona Escape",
            destination = "Barcelona, Spain",
            status = "Planning",
            startDate = "03 Apr 2026",
            endDate = "07 Apr 2026",
            summary = "Beach, architecture and relaxed evenings by the sea.",
            accommodation = "Hotel Jazz Barcelona",
            transport = "High-speed train",
            travelers = 3,
            budgetEur = 780.0,
            activities = listOf(
                Activity("10:00", "Sagrada Familia visit", "Eixample", 26.0),
                Activity("15:00", "Beach and snack", "Barceloneta", 12.0),
                Activity("21:00", "Tapas dinner", "El Born", 28.0),
                Activity("23:00", "Night viewpoint", "Bunkers del Carmel", 9.0)
            ),
            photos = listOf(
                Photo("p1", "Morning route", "Passeig de Gracia", android.R.drawable.ic_menu_mapmode),
                Photo("p2", "Golden hour", "Barceloneta", android.R.drawable.ic_menu_gallery),
                Photo("p3", "Gaudi details", "Sagrada Familia", android.R.drawable.ic_menu_camera)
            )
        ),
        Trip(
            id = "t3",
            title = "Paris Museum Day",
            destination = "Paris, France",
            status = "Booked",
            startDate = "10 May 2026",
            endDate = "11 May 2026",
            summary = "A museum-focused city break with metro passes and a late dinner.",
            accommodation = "Hotel des Arts Montmartre",
            transport = "Morning flight and metro pass",
            travelers = 2,
            budgetEur = 350.0,
            activities = listOf(
                Activity("11:00", "Louvre ticket", "Rue de Rivoli", 22.0),
                Activity("14:00", "Metro day pass", "Paris Metro", 9.0),
                Activity("17:30", "Orsay quick visit", "Left Bank", 16.0),
                Activity("20:30", "Bistro dinner", "Saint-Germain", 31.0)
            ),
            photos = listOf(
                Photo("p1", "Museum pass", "Louvre", android.R.drawable.ic_menu_report_image),
                Photo("p2", "Seine view", "Pont Neuf", android.R.drawable.ic_menu_slideshow),
                Photo("p3", "Evening cafe", "Saint-Germain", android.R.drawable.ic_menu_gallery)
            )
        ),
        Trip(
            id = "t4",
            title = "Lisbon Food Notes",
            destination = "Lisbon, Portugal",
            status = "Draft",
            startDate = "20 Jun 2026",
            endDate = "23 Jun 2026",
            summary = "A mock foodie plan with tram rides, viewpoints and local pastries.",
            accommodation = "LX Boutique Hotel",
            transport = "Budget airline and city tram",
            travelers = 4,
            budgetEur = 610.0,
            activities = listOf(
                Activity("08:30", "Pastel de nata stop", "Belem", 8.0),
                Activity("12:00", "Tram 28 route", "Alfama", 4.0),
                Activity("18:00", "Sunset miradouro", "Santa Luzia", 0.0),
                Activity("21:15", "Seafood dinner", "Cais do Sodre", 42.0)
            ),
            photos = listOf(
                Photo("p1", "Yellow tram", "Alfama", android.R.drawable.ic_menu_directions),
                Photo("p2", "Belvedere", "Santa Luzia", android.R.drawable.ic_menu_compass),
                Photo("p3", "Dinner table", "Ribeira", android.R.drawable.ic_menu_camera)
            )
        )
    )

    fun tripById(id: String): Trip? = trips.firstOrNull { it.id == id }

    fun allPhotos(): List<Photo> = trips.flatMap { it.photos }
}
