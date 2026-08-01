package com.smart.credit.analyzer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.Material3ColorSchemeExtending
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

/**
 * 征信分析应用主题 - 专业金融配色方案
 */
object CreditAnalyzerTheme {

    // 主色调 - 深蓝色代表专业和信任
    private val PrimaryDark = Color(0xFF1A237E)
    private val PrimaryLight = Color(0xFF3949AB)
    private val Primary = Color(0xFF5C6BC0)

    // 辅助色 - 绿色代表增长和正面
    private val Secondary = Color(0xFF2E7D32)
    private val SecondaryVariant = Color(0xFF1B5E20)

    // 错误色 - 红色代表风险警示
    private val Error = Color(0xFFB71C1C)
    private val ErrorVariant = Color(0xFFB33939)

    // 背景色
    private val BackgroundDark = Color(0xFFFAFAFA)
    private val SurfaceDark = Color(0xFFFFFFFF)

    /**
     * 根据系统主题获取颜色方案
     */
    private fun getSystemColorScheme(isDark: Boolean): ColorScheme {
        if (isDark) {
            return Material3ColorSchemeExtending(
                primary = PrimaryDark,
                secondary = Secondary,
                tertiary = Primary,
                error = Error,
                background = BackgroundDark,
                surface = SurfaceDark
            )
        } else {
            return Material3ColorSchemeExtending(
                primary = PrimaryLight,
                secondary = Secondary,
                tertiary = Primary,
                error = Error,
                background = BackgroundDark,
                surface = SurfaceDark
            )
        }
    }

    /**
     * 动态颜色方案（Android 12+）
     */
    fun dynamicColorScheme(isDark: Boolean): ColorScheme {
        return if (isDynamicColorSupported()) {
            if (isDark) dynamicDarkColorScheme() else dynamicLightColorScheme()
        } else {
            getSystemColorScheme(isDark)
        }
    }

    private fun isDynamicColorSupported(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
    }

    /**
     * 应用主题 - Composable函数
     */
    @Composable
    fun CreditAnalyzerTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
    ) {
        val colorScheme = if (darkTheme) getSystemColorScheme(true) else getSystemColorScheme(false)

        MaterialTheme(
            colors = colorScheme,
            shapes = ShapesDefaults.medium,
            typography = TypographyDefaults.text,
            content = content
        )
    }

    /**
     * 预览用的浅色主题
     */
    @Preview(showBackground = true, name = "Light Theme")
    @Composable
    fun LightThemePreview() {
        CreditAnalyzerTheme(darkTheme = false) {
            // 可以在这里添加预览内容
        }
    }

    /**
     * 预览用的深色主题
     */
    @Preview(showBackground = true, name = "Dark Theme", uiMode = UI_MODE_NIGHT_YES)
    @Composable
    fun DarkThemePreview() {
        CreditAnalyzerTheme(darkTheme = true) {
            // 可以在这里添加预览内容
        }
    }
}

// 扩展类以支持Material3颜色方案扩展
private class Material3ColorSchemeExtending(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val error: Color,
    val background: Color,
    val surface: Color
) : androidx.compose.material3.ColorScheme by androidx.compose.material3.lightColorScheme(
    primary = primary,
    secondary = secondary,
    tertiary = tertiary,
    error = error,
    background = background,
    surface = surface
)