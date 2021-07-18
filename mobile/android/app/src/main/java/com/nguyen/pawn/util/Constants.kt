package com.nguyen.pawn.util

import com.nguyen.pawn.model.Language

object Constants {
    val supportedLanguages = listOf(
        Language("en_US", "English"),
        Language("es", "Spanish"),
        Language("fr", "French"),
        Language("de", "German"),
    )

    const val apiURL = "http://192.168.0.239:4000"

}