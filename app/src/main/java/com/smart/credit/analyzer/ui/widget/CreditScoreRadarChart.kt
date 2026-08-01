package com.smart.credit.analyzer.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.credit.analyzer.domain.ScoreBreakdown

/**
 * 信用评分环形图
 */
@Composable
fun CreditScoreCircularBar(
    score: Int,
    modifier: Modifier = Modifier,
    showPercentage: Boolean = true
) {
    val progress by remember { derivedStateOf { (score.toFloat() / 900f).coerceIn(0f, 1f) } }

    Box(
        modifier = modifier
            .size(150.dp)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0))
        )

        CreditArcProgress(progress = progress)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = score.toString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    score >= 750 -> MaterialTheme.colorScheme.secondary
                    score >= 600 -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.error
                }
            )
            if (showPercentage) {
                Text(
                    text = "${(progress * 100).roundToInt()}%",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CreditArcProgress(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = CircleShape
            )
    )
}

/**
 * 信用维度评分条形图
 */
@Composable
fun ScoreDimensionBarChart(
    breakdown: ScoreBreakdown,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "评分维度分解",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            ScoreDimensionItem("还款历史 (35%)", breakdown.paymentHistory, 35)
            ScoreDimensionItem("信用使用率 (30%)", breakdown.creditUtilization, 30)
            ScoreDimensionItem("信用年限 (15%)", breakdown.creditLength, 15)
            ScoreDimensionItem("信用类型 (10%)", breakdown.creditMix, 10)
            ScoreDimensionItem("新信贷申请 (10%)", breakdown.newCredits, 10)

            Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)

            Box(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "总分", fontWeight = FontWeight.Bold)
                Text(
                    text = "${breakdown.totalScore}分",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * 单项维度条形图组件
 */
@Composable
fun ScoreDimensionItem(
    label: String,
    actualScore: Int,
    maxValue: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurface)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.CenterEnd
        ) {
            Spacer(
                modifier = Modifier
                    .width((actualScore.toFloat() / maxValue * 100).dp)
                    .height(10.dp)
                    .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(12.dp))
            )
            Text(
                text = "$actualScore/$maxValue",
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

/**
 * 风险等级指示器
 */
@Composable
fun RiskLevelIndicator(
    score: Int,
    riskLevel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "风险等级", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = riskLevel,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (riskLevel) {
                        "低风险" -> MaterialTheme.colorScheme.secondary
                        "中风险" -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            }

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        color = when (riskLevel) {
                            "低风险" -> MaterialTheme.colorScheme.secondary
                            "中风险" -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.error
                        },
                        alpha = 0.3f
                    )
            )
        }
    }
}
