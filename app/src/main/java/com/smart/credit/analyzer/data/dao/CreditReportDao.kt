package com.smart.credit.analyzer.data.dao

import androidx.room.*
import com.smart.credit.analyzer.data.entity.*
import java.time.LocalDate

/**
 * 征信报告数据访问对象
 */
@Dao
interface CreditReportDao {

    /**
     * 插入或更新征信报告
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: CreditReportEntity): Long

    /**
     * 插入单个信贷账户
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: CreditAccountEntity): Long

    /**
     * 批量插入信贷账户
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<CreditAccountEntity>)

    /**
     * 根据报告ID获取完整征信报告
     */
    @Query("SELECT * FROM credit_reports WHERE reportId = :reportId")
    suspend fun getReportById(reportId: String): CreditReportEntity?

    /**
     * 获取所有征信报告
     */
    @Query("SELECT * FROM credit_reports ORDER BY queryDate DESC")
    suspend fun getAllReports(): List<CreditReportEntity>

    /**
     * 删除报告
     */
    @Delete
    suspend fun delete(report: CreditReportEntity)

    @Query("DELETE FROM credit_reports WHERE reportId = :reportId")
    suspend fun deleteById(reportId: String)

    /**
     * 查询特定日期范围内的报告
     */
    @Query("SELECT * FROM credit_reports WHERE queryDate BETWEEN :startDate AND :endDate ORDER BY queryDate DESC")
    suspend fun getReportsBetweenDates(startDate: LocalDate, endDate: LocalDate): List<CreditReportEntity>

    /**
     * 获取按信用评分排序的报告列表
     */
    @Query("SELECT * FROM credit_reports ORDER BY creditScore DESC")
    suspend fun getReportsByScoreDescending(): List<CreditReportEntity>

    /**
     * 更新报告分数
     */
    @Update
    suspend fun update(report: CreditReportEntity)

    @Query("UPDATE credit_report SET creditScore = :score WHERE reportId = :reportId")
    suspend fun updateScore(reportId: String, score: Int)

    // 账户相关操作
    @Query("SELECT * FROM credit_accounts WHERE reportId = :reportId")
    suspend fun getAccountsByReport(reportId: String): List<CreditAccountEntity>

    @Query("DELETE FROM credit_accounts WHERE reportId = :reportId")
    suspend fun deleteAccountsByReport(reportId: String)

    // 还款记录相关
    @Query("SELECT * FROM payment_records WHERE accountId = :accountId")
    suspend fun getPaymentsByAccount(accountId: String): List<PaymentRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentRecordEntity): Long

    @Query("DELETE FROM payment_records WHERE accountId = :accountId")
    suspend fun deletePaymentsByAccount(accountId: String)
}