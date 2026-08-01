#!/bin/bash

cd /data/user/0/com.ai.assistance.operit/files/workspace/6f15452f-ff12-4185-91f9-5d1d8855d280

# 替换所有aapt2为ARM64版本
echo "正在替换aapt2..."
find ~/.gradle/caches -name 'aapt2' -type f 2>/dev/null | while read f; do
    cp /root/Android/build-tools/36.0.0/aapt2 "$f" 2>/dev/null
    chmod +x "$f" 2>/dev/null
done

# 设置环境变量
export PATH="/data/user/0/com.ai.assistance.operit/files/workspace/6f15452f-ff12-4185-91f9-5d1d8855d280:$PATH"
export ANDROID_AAPT2="/data/user/0/com.ai.assistance.operit/files/workspace/6f15452f-ff12-4185-91f9-5d1d8855d280/aapt2-wrapper.sh"

# 构建
echo "开始构建..."
./gradlew assembleDebug --no-daemon
