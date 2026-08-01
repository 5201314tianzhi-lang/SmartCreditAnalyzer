// Top-level build file
plugins {
    id("com.android.application") version "7.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
}

// Add a task to replace aapt2 before build
tasks.register<Exec>("replaceAapt2") {
    group = "build"
    description = "Replace x86_64 aapt2 with ARM64 version"
    commandLine = listOf(
        "bash", "-c",
        "find ~/.gradle/caches/transforms-3 -name 'aapt2-*' -type d 2>/dev/null | while read dir; do " +
        "aapt2_file=\"\$dir/aapt2\"; " +
        "if [ -f \"\$aapt2_file\" ]; then " +
        "current_arch=\$(file \"\$aapt2_file\" 2>/dev/null | grep -o 'x86-64\\|x86_64' | head -1); " +
        "if [ -n \"\$current_arch\" ]; then " +
        "echo \"Replacing aapt2 in \$dir\"; " +
        "cp /root/Android/build-tools/36.0.0/aapt2 \"\$aapt2_file\"; " +
        "fi; fi; done"
    )
}

// Hook into the build lifecycle
val allTasks = gradle.taskGraph.allTasks
allTasks.forEach { task ->
    if (task.name.contains("processDebugResources")) {
        task.dependsOn("replaceAapt2")
    }
}
