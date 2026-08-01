package com.smart.credit.analyzer.util.pdf

import com.smart.credit.analyzer.data.model.*
import java.time.LocalDate

/**
 * 征信报告PDF解析器 - 简化版本
 * 用于演示目的，实际应用中需要完整的PDF解析库
 */
class CreditReportPdfParser {

    /**
     * 解析PDF征信报告，返回模拟数据
     * @param pdfPath PDF文件路径
     * @return 解析后的CreditReport对象
     */
    fun parse(pdfPath: String): CreditReport {
        return CreditReport(
            reportId = "PDF_${System.currentTimeMillis()}",
            personName = "张三",
            idCardNumber = "110101199001011234",
            reportDate = LocalDate.now(),
            queryDate = LocalDate.now(),
            personalInfo = PersonalInfo(
                gender = "男",
                age = 34,
                occupation = "软件工程师",
                annualIncome = 250000.0,
                residenceArea = "北京市朝阳区",
                employmentYears = 8
            ),
            creditAccounts = listOf(
                CreditAccount(
                    accountType = "信用卡",
                    bankName = "工商银行",
                    accountNumber = "6222****1234",
                    limitAmount = 50000.0,
                    currentBalance = 5000.0,
                    utilizationRate = 10.0,
                    openDate = LocalDate.now().minusYears(5),
                    paymentHistory = emptyList(),
                    status = "正常"
                )
            ),
            inquiries = emptyList(),
            publicRecords = emptyList(),
            riskAnalysis = RiskAnalysis()
        )
    }
}

/**
 * PDF解析异常类
 */
class PdfParsingException(message: String, cause: Throwable?) : RuntimeException(message, cause)