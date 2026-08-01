package com.smart.credit.analyzer.data.entity

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Room数据库类型转换器
 */
object TypeConverters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    @JvmStatic
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    @JvmStatic
    fun toLocalDate(dateStr: String?): LocalDate? {
        return dateStr?.let { LocalDate.parse(it, formatter) }
    }
}