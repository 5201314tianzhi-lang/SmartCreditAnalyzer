package com.smart.credit.analyzer.domain

import com.smart.credit.analyzer.data.model.*
import java.time.Period
import java.time.LocalDate
import java.time.LocalDate

/**
 * 信用评分计算器 - 使用加权评分模型
 */
object CreditScoreCalculator {

    /**
     * 计算综合信用评分 (300-900分)
     */
    fun calculateScore(report: CreditReport): ScoreBreakdown {
        val breakdown = ScoreBreakdown()

        // 1. 还款历史 (35%)
        breakdown.paymentHistory = calculatePaymentHistoryScore(report.creditAccounts)

        // 2. 信用使用率 (30%)
        breakdown.creditUtilization = calculateUtilizationScore(report.creditAccounts)

        // 3. 信用年限 (15%)
        breakdown.creditLength = calculateAgeOfCreditScore(report.creditAccounts)

        // 4. 信用类型组合 (10%)
        breakdown.creditMix = calculateCreditMixScore(report.creditAccounts)

        // 5. 新信贷申请 (10%)
        breakdown.newCredits = calculateNewCreditScore(report.inquiries, report.publicRecords)

        // 计算总分
        breakdown.totalScore = ((breakdown.paymentHistory * 0.35) +
                (breakdown.creditUtilization * 0.30) +
                (breakdown.creditLength * 0.15) +
                (breakdown.creditMix * 0.10) +
                (breakdown.newCredits * 0.10)).roundToInt()

        // 限制在300-900范围内
        breakdown.totalScore = maxOf(300, minOf(900, breakdown.totalScore))

        return breakdown
    }

    /**
     * 计算还款历史得分
     */
    private fun calculatePaymentHistoryScore(accounts: List<CreditAccount>): Int {
        var totalPayments = 0
        var onTimePayments = 0

        accounts.forEach { account ->
            account.paymentHistory.forEach { payment ->
                totalPayments++
                if (payment.isOnTime) onTimePayments++
            }
        }

        if (totalPayments == 0) return 90 // 无还款记录，给予基础分

        val ratio = onTimePayments.toDouble() / totalPayments
        return when {
            ratio >= 0.95 -> 100
            ratio >= 0.90 -> 90
            ratio >= 0.80 -> 70
            ratio >= 0.60 -> 50
            else -> 30
        }
    }

    /**
     * 计算信用使用率得分
     */
    private fun calculateUtilizationScore(accounts: List<CreditAccount>): Int {
        val utilizationRates = accounts
            .filter { it.limitAmount > 0 }
            .map { it.utilizationRate }

        if (utilizationRates.isEmpty()) return 90 // 无授信额度视为优质客户

        val avgUtilization = utilizationRates.average()

        return when {
            avgUtilization <= 10 -> 100
            avgUtilization <= 20 -> 90
            avgUtilization <= 30 -> 80
            avgUtilization <= 40 -> 60
            avgUtilization <= 50 -> 50
            avgUtilization <= 70 -> 30
            else -> 10
        }
    }

    /**
     * 计算信用年限得分
     */
    private fun calculateAgeOfCreditScore(accounts: List<CreditAccount>): Double {
        if (accounts.isEmpty()) return 30 // 无信用记录

        val ages = accounts.map { account ->
            Period.between(account.openDate, LocalDate.now()).months.toDouble()
        }
        val avgAge = ages.average()

        return when {
            avgAge >= 60 -> 100 // 5年以上
            avgAge >= 36 -> 80 // 3-5年
            avgAge >= 24 -> 60 // 2-3年
            avgAge >= 12 -> 40 // 1-2年
            else -> 20 // 不足1年
        }
    }

    /**
     * 计算信用类型组合得分
     */
    private fun calculateCreditMixScore(accounts: List<CreditAccount>): Int {
        val accountTypes = accounts.map { it.accountType }.distinct().count()
        return when (accountTypes) {
            in 4..Int.MAX_VALUE -> 100 // 多种信用类型
            in 3 -> 90
            in 2 -> 70
            else -> 50 // 单一信用类型
        }
    }

    /**
     * 计算新信贷申请得分
     */
    private fun calculateNewCreditScore(inquiries: List<CreditInquiry>, publicRecords: List<PublicRecord>): Int {
        // 统计最近12个月内的硬查询
        val recentHardQueries = inquiries
            .filter { it.inquiryType == "硬查询" }
            .filter { query ->
                Period.between(query.inquiryDate, LocalDate.now()).months <= 12
            }

        // 如果有公共记录中的未处理事项，大幅扣分
        val unresolvedPublicRecords = publicRecords.filter { it.status == "未处理" }.count()

        return when {
            unresolvedPublicRecords > 0 -> 10 // 有未处理严重问题
            recentHardQueries.size >= 5 -> 20 // 太多近期硬查询
            recentHardQueries.size >= 3 -> 40
            recentHardQueries.size >= 2 -> 60
            recentHardQueries.size >= 1 -> 80
            else -> 100 // 无新申请
        }
    }

    /**
     * 评估债务收入比
     */
    fun calculateDebtToIncomeRatio(report: CreditReport): Double {
        if (report.personalInfo.annualIncome <= 0) return 100.0

        val totalMonthlyDebt = report.creditAccounts.sum {
            it.currentBalance + (it.limitAmount * 0.05) // 预估最低还款
        }

        val annualDebt = totalMonthlyDebt * 12
        return (annualDebt / report.personalInfo.annualIncome) * 100
    }

    /**
     * 获取风险等级
     */
    fun getRiskLevel(score: Int): String {
        return when {
            score >= 750 -> "低风险"
            score >= 600 -> "中风险"
            else -> "高风险"
        }
    }

    /**
     * 生成改进建议
     */
    fun generateImprovementSuggestions(report: CreditReport): List<String> {
        val suggestions = mutableListOf<String>()

        // 检查高使用率
        val avgUtilization = report.creditAccounts
            .filter { it.limitAmount > 0 }
            .averageBy { it.utilizationRate }
        if (avgUtilization > 30) {
            suggestions.add("降低信用卡使用率至30%以下，建议及时还款以降低信用使用率")
        }

        // 检查逾期记录
        val lateCount = report.creditAccounts.sum {
            it.paymentHistory.count { !it.isOnTime }
        }
        if (lateCount > 0) {
            suggestions.add("尽快结清逾期货款，并保持未来按时还款")
        }

        // 检查硬查询过多
        val hardQueries = report.inquiries.count { it.inquiryType == "硬查询" }
        if (hardQueries >= 3) {
            suggestions.add("避免短期内频繁申请贷款或信用卡，每次硬查询都会影响信用评分")
        }

        // 检查信用历史过短
        val accountAges = report.creditAccounts.map {
            Period.between(it.openDate, LocalDate.now()).months
        }.average()
        if (accountAges < 12) {
            suggestions.add("保持现有账户正常使用，不要关闭较早的账户以延长信用历史")
        }

        // 检查信用类型单一
        val uniqueTypes = report.creditAccounts.map { it.accountType }.distinct().count()
        if (uniqueTypes < 2) {
            suggestions.add("尝试建立不同类型的信用关系（如信用卡、分期付款等）以丰富信用档案")
        }

        if (suggestions.isEmpty()) {
            suggestions.add("信用状况良好，继续保持现有的良好还款习惯")
        }

        return suggestions
    }

    private fun Double.roundToInt(): Int = Math.round(this.toFloat()).toInt()
}

/**
 * 扩展函数：列表的平均值
 */
fun List<CreditAccount>.averageBy(selector: (CreditAccount) -> Double): Double {
    if (this.isEmpty()) return 0.0
    return this.sumByDouble { selector(it) } / this.size
}