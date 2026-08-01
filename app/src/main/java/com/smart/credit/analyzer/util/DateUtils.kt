package com.smart.credit.analyzer.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 日期工具类
 */
object DateUtils {

    private val DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM")

    /**
     * 格式化日期为 yyyy-MM-dd
     */
    fun formatDate(date: LocalDate): String = date.format(DEFAULT_FORMATTER)

    /**
     * 格式化日期为 yyyy-MM
     */
    fun formatYearMonth(date: LocalDate): String = date.format(YEAR_MONTH_FORMATTER)

    /**
     * 解析字符串为 LocalDate
     */
    fun parseToDate(str: String, pattern: String = "yyyy-MM-DD"): LocalDate? {
        try {
            return LocalDate.parse(str, DateTimeFormatter.ofPattern(pattern))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 获取当前日期
     */
    fun now(): LocalDate = LocalDate.now()

    /**
     * 计算两个日期之间的月份数
     */
    fun monthsBetween(start: LocalDate, end: LocalDate): Long {
        return java.time.Period.between(start, end).months.toLong() + 
               12 * java.time.Period.between(start, end).years.toLong()
    }

    /**
     * 计算年龄（从出生日期到今天的年数）
     */
    fun calculateAge(birthDate: LocalDate): Int {
        return java.time.Period.between(birthDate, now()).years
    }
}