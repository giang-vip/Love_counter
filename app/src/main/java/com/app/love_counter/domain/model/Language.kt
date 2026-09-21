package com.app.love_counter.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Language(
    val code: String,

    @StringRes
    val nameRes: Int,

    @DrawableRes
    val flagRes: Int,

    val isSelected: Boolean = false
)