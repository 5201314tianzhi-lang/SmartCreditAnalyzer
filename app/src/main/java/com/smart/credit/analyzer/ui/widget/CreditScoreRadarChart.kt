import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.smart.credit.analyzer.domain.ScoreBreakdown
import com.smart.credit.analyzer.presentation.model.CreditReportUiModel
import com.smart.credit.analyzer.ui.theme.CreditAnalyzerTheme

/**
 * 信用评分环形图 - 以环形进度条形式展示总得分
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
        // 背景环
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0))
        )

        // 进度环
        CreditArcProgress(
            progress = progress,
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
        )

        // 中心文本
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

/**
 * 环形进度条绘制（自定义Compose实现）
 */
@Composable
private fun CreditArcProgress(
    progress: Float,
    modifier: Modifier = Modifier
) {
    // 这里简化实现，实际可使用Canvas绘制圆形进度条
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .overlayGradient(progress)
    )
}

/**
 * 信用维度评分条形图 - 展示各维度的具体得分
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

            // 还款历史
            ScoreDimensionItem("还款历史 (35%)", breakdown.paymentHistory, 35)

            // 信用使用率
            ScoreDimensionItem("信用使用率 (30%)", breakdown.creditUtilization, 30)

            // 信用年限
            ScoreDimensionItem("信用年限 (15%)", breakdown.creditLength, 15)

            // 信用类型组合
            ScoreDimensionItem("信用类型 (10%)", breakdown.creditMix, 10)

            // 新信贷申请
            ScoreDimensionItem("新信贷申请 (10%)", breakdown.newCredits, 10)

            // 总分分隔线
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)

            // 总分
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
                .background(Color(0xFFE0E0E0), Shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.CenterEnd
        ) {
            Spacer(
                modifier = Modifier
                    .width((actualScore.toFloat() / maxValue * 100).dp)
                    .height(10.dp)
                    .background(MaterialTheme.colorScheme.secondary, Shape = RoundedCornerShape(12.dp))
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
 * 风险等级指示器卡片
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

            // 圆形徽章
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

/**
 * 扩展函数：创建Color State List辅助
 */
fun Color?.overlayGradient(progress: Float): Modifier {
    return this?.let { color ->
        Modifier.graphicsLayer {
            blendMode = android.graphics.BlendMode.Screen
        }
    } ?: Modifier
}

@Preview(showBackground = true)
@Composable
fun CreditScoreCircularBarPreview() {
    CreditAnalyzerTheme {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CreditScoreCircularBar(score = 752)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreDimensionBarChartPreview() {
    val sampleScore = ScoreBreakdown(
        paymentHistory = 85,
        creditUtilization = 70,
        creditLength = 60,
        creditMix = 80,
        newCredits = 90,
        totalScore = 77
    }
    CreditAnalyzerTheme {
        ScoreDimensionBarChart(sampleScore)
    }
}

@Preview(showBackground = true)
@Composable
fun RiskLevelIndicatorPreview() {
    CreditAnalyzerTheme {
        RiskLevelIndicator(score = 752, riskLevel = "低风险")
    }
}