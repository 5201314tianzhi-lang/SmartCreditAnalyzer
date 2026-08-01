package com.smart.credit.analyzer.data.model

import java.time.LocalDate

/**
 * 征信报告主数据模型
 */
data class CreditReport(
    val reportId: String,
    val personName: String,
    val idCardNumber: String,
    val reportDate: LocalDate,
    val queryDate: LocalDate,
    val creditScore: Int = 0,
    val personalInfo: PersonalInfo = PersonalInfo(),
    val creditAccounts: List<CreditAccount> = emptyList(),
    val inquiries: List<CreditInquiry> = emptyList(),
    publicRecords: List<PublicRecord> = emptyList(),
    val riskAnalysis: RiskAnalysis = RiskAnalysis()
)

/**
 * 个人信息
 */
data class PersonalInfo(
    var gender: String = "",
    var age: Int = 0,
    var occupation: String = "",
    var annualIncome: Double = 0.0,
    var residenceArea: String = "",
    var employmentYears: Int = 0
)

/**
 * 信贷账户信息
 */
data class CreditAccount(
    val accountType: String, // "信用卡" / "贷款" / "房贷" / "车贷"
    val bankName: String,
    val accountNumber: String,
    val limitAmount: Double, // 授信额度
    val currentBalance: Double, // 当前余额
    val utilizationRate: Double, // 使用率 (%)
    val openDate: LocalDate,
    val paymentHistory: List<PaymentRecord> = emptyList(),
    val status: String = "正常" // "正常" / "逾期" / "冻结" / "结清"
)

/**
 * 还款记录
 */
data class PaymentRecord(
    val month: String, // "YYYY-MM"
    val dueDate: LocalDate,
    val paymentAmount: Double,
    val isOnTime: Boolean,
    val remarks: String = ""
)

/**
 * 查询记录（征信查询）
 */
data class CreditInquiry(
    val inquiryDate: LocalDate,
    val institutionName: String,
    val inquiryType: String, // "硬查询" / "软查询"
    val purpose: String // "信用卡审批" / "贷款审批" / "其他"
)

/**
 * 公共记录（违约、法院判决等）
 */
data class PublicRecord(
    val recordId: String,
    val recordType: String, // "欠税" / "法院判决" / "行政处罚" / "执行信息"
    val amount: Double,
    val发生Date: LocalDate,
    val status: String // "未处理" / "已处理" / "已执行"
)

/**
 * 风险分析结果
 */
data class RiskAnalysis(
    var overallRiskLevel: String = "低风险", // "低风险" / "中风险" / "高风险"
    var debtToIncomeRatio: Double = 0.0, // 债务收入比
    var creditUtilizationAvg: Double = 0.0, // 平均信用使用率
    var latePaymentCount: Int = 0, // 逾期次数
    var hardQueryCount: Int = 0, // 硬查询次数
    var ageOfCreditAccounts: Double = 0.0, // 账户平均年龄（月）
    var suggestedImprovements: List<String> = emptyList()
)

/**
 * 信用评分详细维度
 */
data class ScoreBreakdown(
    var paymentHistory: Int = 35, // 还款历史 35%
    var creditUtilization: Int = 30, // 信用使用率 30%
    var creditLength: Int = 15, // 信用年限 15%
    var creditMix: Int = 10, // 信用类型组合 10%
    var newCredits: Int = 10, // 新开户 10%
    var totalScore: Int = 0 // 总分
)