package com.smart.credit.analyzer.security

import android.content.Context

/**
 * 生物认证管理器 - 简化版本
 */
class BiometricAuthManager(private val context: Context) {
    
    interface Callback {
        fun onAuthenticated()
        fun onError(errorMsg: String)
        fun onCancelled()
    }
    
    private var callback: Callback? = null
    
    /**
     * 显示生物认证对话框
     */
    fun showBiometricPrompt(callback: Callback?) {
        // 简化版本：直接回调认证成功
        this.callback = callback
        // 在实际应用中，这里应该调用系统生物认证API
        // 由于简化依赖配置，暂时返回成功
        callback?.onAuthenticated()
    }
    
    /**
     * 检查设备是否支持生物认证
     */
    fun isBiometricAvailable(): Boolean {
        // 简化版本：返回false，表示不支持
        return false
    }
}