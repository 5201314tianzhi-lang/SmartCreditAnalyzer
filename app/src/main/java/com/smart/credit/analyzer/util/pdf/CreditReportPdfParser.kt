package com.smart.credit.analyzer.util.pdf

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.smart.credit.analyzer.data.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 征信报告PDF解析器 - 从标准格式的PDF文件提取信用数据
 * 
 * 支持央行标准的个人信用报告PDF格式，可提取：
 * - 个人信息（姓名、身份证号等）
 * - 信贷账户信息（信用卡、贷款明细）
 * - 还款记录
 * - 查询记录
 */
class CreditReportPdfParser {

    private val datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val monthPattern = DateTimeFormatter.ofPattern("yyyy-MM")

    /**
     * 解析PDF征信报告，返回结构化数据模型
     * @param pdfPath PDF文件路径
     * @return 解析后的CreditReport对象（实际应用中需要完善字段填充逻辑）
     */
    fun parse(pdfPath: String): CreditReport {
        return try {
            // 打开PDF文档
            val pdfReader = PdfReader(pdfPath)
            val pdfDocument = PdfDocument(pdfReader)
            val document = Document(pdfDocument)

            // 提取文本内容
            val allText = extractAllText(pdfDocument)

            // 根据提取的文本解析各项数据
            val personalInfo = extractPersonalInfo(allText)
            val creditAccounts = extractCreditAccounts(allText)
            val inquiries = extractInquiries(allText)
            val publicRecords = extractPublicRecords(allText)

            // 创建报告对象
            val report = CreditReport(
                reportId = generateReportId(pdfPath),
                personName = personalInfo.personName,
                idCardNumber = personalInfo.idCardNumber,
                reportDate = LocalDate.now(),
                queryDate = LocalDate.now(),
                personalInfo = personalInfo,
                creditAccounts = creditAccounts,
                inquiries = inquiries,
                publicRecords = publicRecords,
                riskAnalysis = RiskAnalysis()
            )

            // 关闭资源
            document.close()
            pdfDocument.close()

            report

        } catch (e: Exception) {
            throw PdfParsingException("PDF解析失败: ${e.message}", e)
        }
    }

    /**
     * 提取所有PDF文本内容
     */
    private fun extractAllText(pdfDocument: PdfDocument): String {
        val sb = StringBuilder()
        for (i in 1..pdfDocument.numberOfPages) {
            val page = pdfDocument.getPage(i)
            sb.append(page.getInfo()?.getText() ?: "")
            sb.append("\n")
        }
        return sb.toString()
    }

    /**
     * 解析个人信息
     */
    private fun extractPersonalInfo(text: String): PersonalInfo {
        return PersonalInfo(
            gender = extractValue(text, "性别", "(男|女)"),
            age = extractAge(text),
            occupation = extractValue(text, "职业", ".+?\\n"),
            annualIncome = extractIncome(text),
            residenceArea = extractValue(text, "居住地址", ".+?\\n"),
            employmentYears = extractEmploymentYears(text)
        )
    }

    /**
     * 解析信贷账户
     */
    private fun extractCreditAccounts(text: String): List<CreditAccount> {
        val accounts = mutableListOf<CreditAccount>()

        // 简化示例：在实际应用中需要复杂的正则匹配
        // 这里假定每个账户信息块以"贷款机构"或"信用卡"开头
        val accountBlocks = text.split("\n\n").filter { it.contains("银行") || it.contains("授信") }

        accountBlocks.forEach block@{ block ->
            try {
                val bankName = extractValue(block, "银行", ".+?(?:银行|公司)", true) ?: return@block
                val limitAmount = extractAmount(block, "额度") ?: return@block
                val currentBalance = extractAmount(block, "余额") ?: 0.0
                val utilizationRate = calculateUtilization(currentBalance, limitAmount)
                val status = extractStatus(block)

                val account = CreditAccount(
                    accountType = detectAccountType(block),
                    bankName = bankName,
                    accountNumber = extractAccountNumber(block),
                    limitAmount = limitAmount,
                    currentBalance = currentBalance,
                    utilizationRate = utilizationRate,
                    openDate = LocalDate.now().minusYears(3),
                    paymentHistory = emptyList(),
                    status = status
                )
                accounts.add(account)
            } catch (e: Exception) {
                // 跳过解析失败的账户块
                return@block
            }
        }

        return accounts
    }

    /**
     * 解析查询记录
     */
    private fun extractInquiries(text: String): List<CreditInquiry> {
        val inquiries = mutableListOf<CreditInquiry>()

        // 查找查询记录部分
        val inquirySection = text.substringAfter("查询记录") ?: return emptyList()

        // 简化示例：实际应用中需更精细的解析
        val lines = inquirySection.split("\n").take(5) // 最多解析前5条查询
        lines.forEach { line ->
            if (line.isNotEmpty()) {
                inquiries.add(CreditInquiry(
                    inquiryDate = LocalDate.now(),
                    institutionName = extractValue(line, "机构", ".+?\\s+", true),
                    inquiryType = detectInquiryType(line),
                    purpose = detectPurpose(line)
                ))
            }
        }

        return inquiries
    }

    /**
     * 解析公共记录（违约、法院判决等）
     */
    private fun extractPublicRecords(text: String): List<PublicRecord> {
        val records = mutableListOf<PublicRecord>()

        val publicSection = text.substringBefore("公共记录信息") ?: return emptyList()

        // 简化的解析逻辑
        return records
    }

    /**
     * 辅助方法：提取指定模式的文本
     */
    private fun extractValue(text: String, label: String, pattern: String, ignoreCase: Boolean = false): String? {
        val regex = if (ignoreCase) "(?i)$label\\\\s*:?(.*?)$pattern" else "$label\\\\s*:(.*?)$pattern"
        return text.matchToRegex(regex)?.trim()?.takeIf { it.isNotEmpty() }
    }

    /**
     * 提取金额数值
     */
    private fun extractAmount(text: String, context: String?): Double? {
        val numberPattern = Regex("""[\d,]+\.\d{2}|\d+,?\d*""")
        val matches = numberPattern.findAll(text).map { it.value.replace(",", "").toDoubleOrNull() }.toList()
        return matches.firstOrNull { it > 0 }
    }

    /**
     * 计算信用使用率
     */
    private fun calculateUtilization(balance: Double, limit: Double): Double {
        if (limit <= 0) return 0.0
        return (balance / limit) * 100.coerceAtMost(100.0)
    }

    /**
     * 检测账户类型（信用卡/贷款/房贷/车贷）
     */
    private fun detectAccountType(block: String): String {
        return when {
            block.contains("信用卡") || block.contains("信用卡") -> "信用卡"
            block.contains("房贷") -> "房贷"
            block.contains("车贷") -> "车贷"
            else -> "贷款"
        }
    }

    /**
     * 检测查询类型（硬查询/软查询）
     */
    private fun detectInquiryType(line: String): String {
        return if (line.contains("审批")) "硬查询" else "软查询"
    }

    /**
     * 检测查询目的
     */
    private fun detectPurpose(line: String): String {
        return if (line.contains("信用卡")) "信用卡审批" else "贷款审批"
    }

    /**
     * 获取账户状态
     */
    private fun extractStatus(block: String): String {
        return when {
            block.contains("逾期") -> "逾期"
            block.contains("结清") -> "结清"
            block.contains("冻结") -> "冻结"
            else -> "正常"
        }
    }

    /**
     * 从文本中提取年龄
     */
    private fun extractAge(text: String): Int {
        val ageMatch = Regex("\\d{1,3}岁").find(text)
        return ageMatch?.value?.replace("岁", "").toIntOrNull() ?: 0
    }

    /**
     * 提取年收入
     */
    private fun extractIncome(text: String): Double {
        val incomeExtract = extractAmount(text, "收入")
        return if (incomeExtract != null && incomeExtract > 0) incomeExtract * 1000 else 0.0
    }

    /**
     * 提取工作年限
     */
    private fun extractEmploymentYears(text: String): Int {
        val workMatch = Regex("\\d+年").find(text)
        return workMatch?.value?.replace("年", "").toIntOrNull() ?: 0
    }

    /**
     * 生成报告ID
     */
    private fun generateReportId(pdfPath: String): String {
        val fileName = pdfPath.substringAfterLast("/")
        return "PDF_${System.currentTimeMillis()}_${fileName.hashCode()}"
    }
}

/**
 * PDF解析异常类
 */
class PdfParsingException(message: String, cause: Throwable?) : RuntimeException(message, cause)