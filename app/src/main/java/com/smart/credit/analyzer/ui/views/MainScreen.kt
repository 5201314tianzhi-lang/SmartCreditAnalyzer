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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import com.smart.credit.analyzer.domain.ScoreBreakdown

/**
 * 主屏幕 - 征信报告分析应用入口
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues()
) {
    CreditAnalyzerScreen(modifier = modifier.padding(padding))
}

@Composable
fun CreditAnalyzerScreen(
    modifier: Modifier = Modifier
) {
    var reports by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedReportId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        reports = listOf("CR_2024001", "CR_2024002", "CR_2024003")
    }

    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        CreditTopBar(
            onNewReportClick = { },
            onScanReportClick = { }
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
    val sampleScore = ScoreBreakdown(
        paymentHistory = 85,
        creditUtilization = 70,
        creditLength = 60,
        creditMix = 80,
        newCredits = 90,
        totalScore = 77
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "返回")
            }
            Text(text = "报告详情", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "信用评分: ${sampleScore.totalScore}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
