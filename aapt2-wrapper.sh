#!/bin/bash
# 包装脚本：将所有aapt2调用重定向到ARM64版本
REAL_AAPT2=/root/Android/build-tools/36.0.0/aapt2
exec "$REAL_AAPT2" "$@"
