package com.smart.credit.analyzer.repository

import android.content.Context
import com.smart.credit.analyzer.data.CreditDatabase
import com.smart.credit.analyzer.data.dao.CreditReportDao
import com.smart.credit.analyzer.data.entity.CreditReportEntity
import com.smart.credit.analyzer.data.model.CreditReport
import com.smart.credit.analyzer.domain.CreditScoreCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CreditReportRepository(private val context: Context) {
    
    private val database: CreditDatabase by lazy {
        CreditDatabase.getInstance(context)
    }
    
    private val dao: CreditReportDao by lazy {
        database.creditReportDao()
    }
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    
    private val calculator = CreditScoreCalculator()
    
    fun getAllReports(): Flow<List<CreditReportEntity>> = dao.getAllReports()
    
    suspend fun loadReport(reportId: String): CreditReport? {
        return dao.getReportById(reportId)?.toCreditReport()
    }
    
    suspend fun saveReport(report: CreditReport): String {
        val id = report.reportId ?: System.currentTimeMillis().toString()
        dao.insertReport(report.toEntity(id))
        return id
    }
    
    suspend fun analyzeReport(report: CreditReport): CreditReport {
        val breakdown = calculator.calculateScore(report)
        report.scoreBreakdown = breakdown
        report.creditScore = breakdown.totalScore
        report.riskLevel = calculator.getRiskLevel(breakdown.totalScore)
        return report
    }
    
    suspend fun updateReport(report: CreditReport) {
        dao.updateReport(report.toEntity(report.reportId ?: return))
    }
    
    suspend fun deleteReport(reportId: String) = dao.deleteReport(reportId)
    
    suspend fun clearAll() = dao.deleteAll()
}