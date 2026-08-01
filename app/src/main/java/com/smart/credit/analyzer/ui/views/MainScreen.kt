package com.smart.credit.analyzer.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.materialicons.outlined.*
import com.smart.credit.analyzer.domain.ScoreBreakdown
import com.smart.credit.analyzer.presentation.model.CreditReportUiModel
import com.smart.credit.analyzer.ui.theme.CreditAnalyzerTheme
import com.smart.credit.analyzer.ui.widget.CreditScoreCircularBar
import com.smart.credit.analyzer.ui.widget.RiskLevelIndicator
import com.smart.credit.analyzer.ui.widget.ScoreDimensionBarChart

/**
 * 主屏幕 - 征信报告分析应用入口
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    padding: BoxPadding = PaddingValues()
) {
    CreditAnalyzerScreen(modifier = modifier.padding(padding))
}

@Composable
fun CreditAnalyzerScreen(
    modifier: Modifier = Modifier
) {

    var reports by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedReportId by remember { mutableStateOf<String?>(null) }

    // 初始化模拟数据
    LaunchedEffect(Unit) {
        reports = listOf("CR_2024001", "CR_2024002", "CR_2024003")
    }

    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        CreditTopBar(
            onNewReportClick = { /* 处理新建报告逻辑 */ },
            onScanReportClick = { /* 处理扫描报告逻辑 */ }
        )

        when {
            selectedReportId == null -> ReportsListScreen(
                reports = reports,
                onReportSelected = { selectedReportId = it }
            )
            else -> ReportDetailScreen(
                reportId = selectedReportId!!,
                onBackClick = { selectedReportId = null }
            )
        }
    }
}

@Composable
fun CreditTopBar(
    onNewReportClick: () -> Unit,
    onScanReportClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "智信分析",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        actions = {
            IconButton(onClick = onScanReportClick) {
                Icon(
                    imageVector = Icons.Outlined.Scan,
                    contentDescription = "扫描",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            IconButton(onClick = onNewReportClick) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "新建",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White
        )
    )
}

@Composable
fun ReportsListScreen(
    reports: List<String>,
    onReportSelected: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(reports) { reportId ->
            ReportItemCard(
                reportId = reportId,
                onClick = { onReportSelected(reportId) }
            )
        }
    }
}

@Composable
fun ReportItemCard(
    reportId: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "报告编号: $reportId",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "信用评分中...",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Outlined.ChevronForward,
                contentDescription = "查看详情"
            )
        }
    }
}
@Composable
fun ReportDetailScreen(
    reportId: String,
    onBackClick: () -> Unit
) {
    // 获取模拟的评分数据（实际应用中应从ViewModel获取）
    val uiModel by remember { mutableStateOf<CreditReportUiModel?>(null) }
    val scoreBreakdown by remember { mutableStateOf<ScoreBreakdown?>(null) }

    LaunchedEffect(Unit) {
        // 模拟从数据库或网络加载数据
        val sampleScore = ScoreBreakdown(
            paymentHistory = 85,
            creditUtilization = 70,
            creditLength = 60,
            creditMix = 80,
            newCredits = 90,
            totalScore = 77
        )
        scoreBreakdown = sampleScore
        uiModel = createSampleUiModel()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 返回按钮
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "返回")
            }
            Text(text = "报告详情", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 信用评分环形图
        CreditScoreCircularBar(
            score = scoreBreakdown?.totalScore ?: 0,
            modifier = Modifier.fillMaxWidth(),
            showPercentage = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 风险等级指示器
        RiskLevelIndicator(
            score = scoreBreakdown?.totalScore ?: 0,
            riskLevel = "低风险",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 维度分解条形图
        scoreBreakdown?.let { breakdown ->
            ScoreDimensionBarChart(breakdown, modifier = Modifier.fillMaxWidth())
        } ?: run {
            // 加载中状态
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Text("正在分析...", style = MaterialTheme.typography.body2)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 改进建议（保留原有功能）
        SuggestionsSection()
    }
}

/**
 * 创建示例UI模型用于演示
 */
private fun createSampleUiModel(): CreditReportUiModel {
    return CreditReportUiModel(
        reportId = "CR_2024001",
        personName = "张三",
        creditScore = 77,
        riskLevel = "低风险",
        reportDate = LocalDate.now(),
        personalInfo = PersonalInfoUiModel(
            gender = "男",
            age = 30,
            occupation = "软件工程师",
            annualIncome = 150000.0,
            residenceArea = "北京市",
            employmentYears = 5
        ),
        creditAccounts = emptyList(),
        scoreBreakdown = ScoreBreakdownUiModel(
            paymentHistory = 85,
            creditUtilization = 70,
            creditLength = 60,
            creditMix = 80,
            newCredits = 90,
            totalScore = 77
        ),
        suggestions = listOf(
            "保持现有信用卡使用率在30%以下",
            "继续按时还款，维持良好记录",
            "不要关闭已有的老账户以延长信用历史",
            "谨慎申请新信贷产品"
        ),
        stats = CreditReportStats(
            debtToIncomeRatio = 25.5,
            avgUtilization = 18.2,
            latePaymentCount = 0,
            hardQueryCount = 2,
            accountAgeMonths = 42.5,
            totalCreditLimit = 50000.0,
            totalOutstanding = 9000.0
        )
    )
}
@Composable
fun ScoreCard(
    creditScore: Int,
    riskLevel: String
) {
    val scoreColor = when {
        creditScore >= 750 -> MaterialTheme.colorScheme.secondary
        creditScore >= 600 -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "信用评分",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = creditScore.toString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = scoreColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            RiskBadge(riskLevel = riskLevel)
        }
    }
}

@Composable
fun RiskBadge(riskLevel: String) {
    val (bgColor, textColor) = when (riskLevel) {
        "低风险" -> (Color.Green, Color.Black) to Color.Black
        "中风险" -> (Color.Yellow, Color.Black) to Color.Black
        else -> (Color.Red, Color.White) to Color.White
    }

    Row(
        modifier = Modifier
            .background(bgColor, CircleShape)
            .padding(6.dp, 16.dp)
            .clip(RoundedCornerShape(20.dp)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = riskLevel,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun StatsGrid() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "信用概况",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                StatItem(label = "使用率", value = "18%")
                StatItem(label = "逾期", value = "0次")
                StatItem(label = "查询", value = "2次")
                StatItem(label = "年限", value = "3.5年")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SuggestionsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "信用提升建议",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            val suggestions = listOf(
                "保持现有信用卡使用率在30%以下",
                "继续按时还款，维持良好记录",
                "不要关闭已有的老账户以延长信用历史",
                "谨慎申请新信贷产品"
            )
            suggestions.forEach { suggestion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "建议项",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = suggestion,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CreditAnalyzerTheme {
        CreditAnalyzerScreen()
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun LightThemePreview() {
    CreditAnalyzerTheme(darkTheme = false) {
        CreditAnalyzerScreen()
    }
}

@Preview(showBackground = true, name = "Dark Theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DarkThemePreview() {
    CreditAnalyzerTheme(darkTheme = true) {
        CreditAnalyzerScreen()
    }
}