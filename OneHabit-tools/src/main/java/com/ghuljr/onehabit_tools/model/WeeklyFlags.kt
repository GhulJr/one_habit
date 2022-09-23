package com.ghuljr.onehabit_tools.model

enum class WeeklyFlags(val bitFlag: Int) {
    DURING_WEEK(0x00000000),
    MONDAY(0x00000001),
    TUESDAY(0x00000010),
    WEDNESDAY(0x00000100),
    THURSDAY(0x00001000),
    FRIDAY(0x00010000),
    SATURDAY(0x00100000),
    SUNDAY(0x01000000)
}