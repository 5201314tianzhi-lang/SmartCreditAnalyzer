#!/bin/bash
# 自动替换所有Gradle缓存中的x86_64 aapt2为ARM64版本

echo "正在替换aapt2..."

# 替换所有缓存中的aapt2
find ~/.gradle/caches -name 'aapt2' -type f 2>/dev/null | while read f; do
    cp /root/Android/build-tools/36.0.0/aapt2 "$f"
    chmod +x "$f"
    echo "已替换: $f"
done

echo "aapt2替换完成！"
