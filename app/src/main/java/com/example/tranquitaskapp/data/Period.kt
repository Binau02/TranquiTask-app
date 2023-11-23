package com.example.tranquitaskapp.data

enum class Period {
    DAY, WEEK, ALL
}

object PeriodDictionary {
    val periodToStringId : HashMap<Period, Int> = hashMapOf()
    val stringToPeriod : HashMap<String, Period> = hashMapOf()
}