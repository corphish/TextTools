package com.corphish.quicktools.data

data class NumberAnalysisResult(
    val input: String,
    val base: Int,
    val isFloatingPoint: Boolean,
    val supportedBases: List<Int>,
    val conversions: List<Pair<Int, String>>,
    val mathTransformations: List<Pair<Int, String>>,
    val digitDerivations: List<Pair<Int, String>>,
    val factors: List<Pair<Int, String>> = emptyList(),
    val primeInfo: List<Pair<Int, String>>,
    val languageConversions: List<Pair<Int, String>>
)
