# 智信分析 SmartCredit Analyzer

一个专业的Android征信报告分析应用，使用Jetpack Compose和Material Design 3构建。

## 功能特性

- 📊 **征信报告分析**：解析PDF格式的征信报告
- 🎯 **信用评分**：5维加权评分模型（300-900分）
  - 还款历史 35%
  - 信用使用率 30%
  - 信用年限 15%
  - 信用类型组合 10%
  - 新信贷申请 10%
- 📈 **可视化图表**：雷达图展示各项指标
- 🔒 **安全加密**：AES-256加密存储敏感数据
- 👆 **生物认证**：支持指纹/面部识别
- 💾 **本地存储**：Room数据库持久化

## 技术栈

- **语言**：Kotlin 100%
- **UI框架**：Jetpack Compose + Material Design 3
- **架构**：MVVM + Clean Architecture
- **数据库**：Room
- **异步**：Coroutines + Flow

## 构建方式

### 本地构建
```bash
./gradlew assembleDebug
```

### GitHub Actions
推送代码到GitHub后，会自动触发CI构建。

## 安装

1. 克隆仓库
2. 使用Android Studio打开
3. 等待依赖下载完成
4. 运行或构建APK

## 系统要求

- Android 7.0+ (API 24)
- JDK 17
- Android Studio Hedgehog 或更高版本

## 许可证

MIT License
