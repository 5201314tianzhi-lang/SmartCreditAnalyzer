package com.smart.credit.analyzer.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Room数据库中的征信报告实体
 */
@Entity(tableName = "credit_reports")
data class CreditReportEntity(
    @PrimaryKey val reportId: String,
    val personName: String,
    val idCardNumber: String,
    val reportDate: LocalDate,
    val queryDate: LocalDate,
    val creditScore: Int,
    val personalInfoJson: String, // JSON格式存储个人信息
    val creditAccountsJson: String, // JSON格式存储信贷账户
    val inquiriesJson: String, // JSON格式存储查询记录
    val publicRecordsJson: String, // JSON格式存储公共记录
    val riskAnalysisJson: String, // JSON格式存储风险分析
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 信贷账户实体（分离表，用于支持一对多关系）
 */
@Entity(tableName = "credit_accounts", 
        foreignField = "reportId",
        parentEntity = CreditReportEntity::class)
data class CreditAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reportId: String,
    val accountType: String,
    val bankName: String,
    val accountNumber: String,
    val limitAmount: Double,
    val currentBalance: Double,
    val utilizationRate: Double,
    val openDate: LocalDate,
    val status: String
)

/**
 * 还款记录实体
 */
@Entity(tableName = "payment_records")
data class PaymentRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: String,
    val month: String,
    val dueDate: LocalDate,
    val paymentAmount: Double,
    val isOnTime: Boolean,
    val remarks: String
)

/**
 * 查询记录实体
 */
@Entity(tableName = "inquiry_records")
data class InquiryRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reportId: String,
    val inquiryDate: LocalDate,
    val institutionName: String,
    val inquiryType: String,
    val purpose: String
)

/**
 * 公共记录实体
 */
@Entity(tableName = "public_record_entities")
data class PublicRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reportId: String,
    val recordId: String,
    val recordType: String,
    val amount: Double,
    val發生Date: LocalDate,
    val status: String
)