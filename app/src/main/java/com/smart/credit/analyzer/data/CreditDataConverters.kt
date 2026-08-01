package com.smart.credit.analyzer.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smart.credit.analyzer.data.entity.*
import com.smart.credit.analyzer.data.model.*
import java.lang.reflect.Type
import java.time.LocalDate

/**
 * 数据层转换器 - 在实体（Entity）和数据模型（Model）之间进行转换
 */
object CreditDataConverters {

    private val gson = Gson()

    /**
     * 将 CreditReportEntity 转换为 CreditReport
     */
    fun toDomain(entity: CreditReportEntity?): CreditReport? {
        if (entity == null) return null

        val personalInfo = gson.fromJson(
            entity.personalInfoJson,
            PersonalInfo::class.java
        )

        val accountsType = object : TypeToken<List<CreditAccount>>() {}.type
        val accounts = gson.fromJson(
            entity.creditAccountsJson,
            accountsType
        ).map { account ->
            account.toDomain()
        }

        val inquiriesType = object : List<CreditInquiry>() {}.type
        val inquiries = gson.fromJson(
            entity.inquiriesJson,
            inquiriesType
        )!!

        val publicRecords = gson.fromJson(
            entity.publicRecordsJson,
            ArrayDeque<PublicRecord>::class.java
        )?.toList() ?: emptyList()

        val riskAnalysisType = object : TypeToken<RiskAnalysis>() {}.type
        val riskAnalysis = gson.fromJson(
            entity.riskAnalysisJson,
            riskAnalysisType
        )

        return CreditReport(
            reportId = entity.reportId,
            personName = entity.personName,
            idCardNumber = entity.idCardNumber,
            reportDate = entity.reportDate,
            queryDate = entity.queryDate,
            creditScore = entity.creditScore,
            personalInfo = personalInfo,
            creditAccounts = accounts,
            inquiries = inquiries,
            publicRecords = publicRecords,
            riskAnalysis = riskAnalysis
        )
    }

    /**
     * 将 CreditReport 转换为 CreditReportEntity
     */
    fun toEntity(report: CreditReport): CreditReportEntity {
        val personalInfoJson = gson.toJson(report.personalInfo)
        val accountsType = object : TypeToken<List<CreditAccount>>() {}.type
        val creditAccountsJson = gson.toJson(report.creditAccounts, accountsType)
        val inquiriesJson = gson.toJson(report.inquiries)
        val publicRecordsJson = gson.toJson(report.publicRecords)
        val riskAnalysisJson = gson.toJson(report.riskAnalysis)

        return CreditReportEntity(
            reportId = report.reportId,
            personName = report.personName,
            idCardNumber = report.idCardNumber,
            reportDate = report.reportDate,
            queryDate = report.queryDate,
            creditScore = report.creditScore,
            personalInfoJson = personalInfoJson,
            creditAccountsJson = creditAccountsJson,
            inquiriesJson = inquiriesJson,
            publicRecordsJson = publicRecordsJson,
            riskAnalysisJson = riskAnalysisJson
        )
    }

    /**
     * 将 CreditAccountEntity 转换为 CreditAccount
     */
    fun CreditAccountEntity.toDomain(): CreditAccount {
        return CreditAccount(
            accountType = accountType,
            bankName = bankName,
            accountNumber = accountNumber,
            limitAmount = limitAmount,
            currentBalance = currentBalance,
            utilizationRate = utilizationRate,
            openDate = openDate,
            paymentHistory = emptyList(), // 需要在另一查询中获取
            status = status
        )
    }

    /**
     * 将 PaymentRecordEntity 转换为 PaymentRecord
     */
    fun PaymentRecordEntity.toDomain(): PaymentRecord {
        return PaymentRecord(
            month = month,
            dueDate = dueDate,
            paymentAmount = paymentAmount,
            isOnTime = isOnTime,
            remarks = remarks
        )
    }
}

/**
 * Gson扩展类型工厂（用于泛型序列化和反序列化）
 */
fun <T> createGsonType(): Type = object : TypeToken<T>() {}.type