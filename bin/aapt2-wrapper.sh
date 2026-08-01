#!/bin/bash
# Wrapper to replace x86_64 aapt2 with ARM64 version
exec /root/Android/build-tools/36.0.0/aapt2 "$@"
