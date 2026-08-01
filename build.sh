#!/bin/bash
set -e
export ANDROID_HOME=/root/Android

echo "=== Cleaning gradle caches ==="
rm -rf ~/.gradle/caches/transforms-3

echo "=== Running gradle build ==="
./gradlew assembleDebug

echo "=== Replacing aapt2 with ARM64 version ==="
find ~/.gradle/caches/transforms-3 -name "aapt2-*" -type d 2>/dev/null | while read dir; do
    aapt2_file="$dir/aapt2"
    if [ -f "$aapt2_file" ]; then
        current_arch=$(file "$aapt2_file" 2>/dev/null | grep -o "x86-64\|x86_64" | head -1)
        if [ -n "$current_arch" ]; then
            echo "Replacing x86_64 aapt2 in $dir"
            cp /root/Android/build-tools/36.0.0/aapt2 "$aapt2_file"
        fi
    fi
done

echo "=== Build complete ==="
ls -la app/build/outputs/apk/debug/*.apk 2>/dev/null || echo "APK not found"
