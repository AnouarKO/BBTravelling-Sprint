package com.example.bbtraveling.domain

import androidx.annotation.DrawableRes

data class Photo(
    val id: String,
    val title: String,
    val spot: String,
    @param:DrawableRes val resId: Int
)
