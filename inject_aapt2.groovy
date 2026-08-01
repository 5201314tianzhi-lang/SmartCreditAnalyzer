// Gradle脚本：在构建过程中自动替换aapt2
gradle.taskGraph.whenReady { taskGraph ->
    taskGraph.allTasks.each { task ->
        if (task.name.contains('Aapt2') || task.name.contains('aapt2') || task.name.contains('ProcessResources')) {
            task.doFirst {
                def gradleHome = System.getenv('HOME') + '/.gradle/caches'
                def aapt2Files = new File(gradleHome).listFiles()?.findAll { it.name == 'aapt2' && it.isFile() }
                aapt2Files?.each { file ->
                    println "Replacing: $file"
                    def src = new File('/root/Android/build-tools/36.0.0/aapt2')
                    if (src.exists()) {
                        file.withOutputStream { out -> src.withInputStream { in -> in.transferTo(out) } }
                        file.setExecutable(true)
                    }
                }
            }
        }
    }
}
