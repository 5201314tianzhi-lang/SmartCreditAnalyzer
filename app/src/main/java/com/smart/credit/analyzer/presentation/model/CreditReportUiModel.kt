package com.smart.credit.analyzer.presentation.model

import com.smart.credit.analyzer.data.model.*
import java.time.LocalDate

/**
 * UI层的信用报告展示模型
 */
data class CreditReportUiModel(
    val reportId: String,
    val personName: String,
    val creditScore: Int,
    val riskLevel: String,
    val reportDate: LocalDate,
    val personalInfo: PersonalInfoUiModel,
    val creditAccounts: List<CreditAccountUiModel>,
    val scoreBreakdown: ScoreBreakdownUiModel,
    val suggestions: List<String>,
    val stats: CreditReportStats
)

/**
 * 个人信息UI模型
 */
data class PersonalInfoUiModel(
    val gender: String,
    val age: Int,
    val occupation: String,
    val annualIncome: Double,
    val residenceArea: String,
    val employmentYears: Int
)

/**
 * 信贷账户UI模型
 */
data class CreditAccountUiModel(
    val accountType: String,
    val bankName: String,
    val limitAmount: Double,
    val currentBalance: Double,
    val utilizationRate: Double,
    val status: String,
    val openDate: LocalDate
)

/**
 * 信用评分分解UI模型
 */
data class ScoreBreakdownUiModel(
    val paymentHistory: Int,
    val creditUtilization: Int,
    val creditLength: Int,
    val creditMix: Int,
    val newCredits: Int,
    val totalScore: Int
)

/**
 * 信用报告统计信息
 */
data class CreditReportStats(
    val debtToIncomeRatio: Double,
    val avgUtilization: Double,
    val latePaymentCount: Int,
    val hardQueryCount: Int,
    val accountAgeMonths: Double,
    val totalCreditLimit: Double,
    val totalOutstanding: Double
)

/**
 * 转换扩展函数
 */
fun CreditReport.toUiModel(): CreditReportUiModel {
    val riskLevel = com.smart.credit.analyzer.domain.CreditScoreCalculator.getRiskLevel(creditScore)
    
    val accountsUi = creditAccounts.map { account ->
        CreditAccountUiModel(
            accountType = account.accountType,
            bankName = account.bankName,
            limitAmount = account.limitAmount,
            currentBalance = account.currentBalance,
            utilizationRate = account.utilizationRate,
            status = account.status,
            openDate = account.openDate
        )
    }

    // 计算统计信息
    val totalLimit = creditAccounts.sumByDouble { it.limitAmount }
    val totalBalance = creditAccounts.sumByDouble { it.currentBalance }
    val avgUtilization = if (creditAccounts.isNotEmpty()) {
        creditAccounts.averageBy { it.utilizationRate }
    } else 0.0
    val lateCount = creditAccounts.sum { it.paymentHistory.count { !it.isOnTime } }
    val hardQueryCount = inquiries.count { it.inquiryType == "硬查询" }
    val accountAge = if (creditAccounts.isNotEmpty()) {
        creditAccounts.averageBy { 
            java.time.Period.between(it.openDate, LocalDate.now()).months.toDouble() 
        }
    } else 0.0

    return CreditReportUiModel(
        reportId = reportId,
        personName = personName,
        creditScore = creditScore,
        riskLevel = riskLevel,
        reportDate = reportDate,
        personalInfo = PersonalInfoUiModel(
            gender = personalInfo.gender,
            age = personalInfo.age,
            occupation = personalInfo.occupation,
            annualIncome = personalInfo.annualIncome,
            residenceArea = personalInfo.residenceArea,
            employmentYears = personalInfo.employmentYears
        ),
        creditAccounts = accountsUi,
        scoreBreakdown = ScoreBreakdownUiModel(
            paymentHistory = 35, // 实际应从domain获取
            creditUtilization = 30,
            creditLength = 15,
            creditMix = 10,
            newCredits = 10,
            totalScore = creditScore
        ),
        suggestions = riskAnalysis.suggestedImprovements,
        stats = CreditReportStats(
            debtToIncomeRatio = riskAnalysis.debtToIncomeRatio,
            avgUtilization = riskAnalysis.creditUtilizationAvg,
            latePaymentCount = riskAnalysis.latePaymentCount,
            hardQueryCount = riskAnalysis.hardQueryCount,
            accountAgeMonths = riskAnalysis.ageOfCreditAccounts,
            totalCreditLimit = totalLimit,
            totalOutstanding = totalBalance
        )
    )
}

// 扩展函数
fun List<CreditAccount>.averageBy(selector: (CreditAccount) -> Double): Double {
    if (this.isEmpty()) return 0.0
    return this.sumByDouble { selector(it) } / this.size
}