package com.example.tranquitaskapp.data

enum class Period {
    DAY, WEEK, ALL
}

object PeriodDictionary {
    val periodToString : HashMap<Period, String> = hashMapOf()
    val stringToPeriod : HashMap<String, Period> = hashMapOf()
}