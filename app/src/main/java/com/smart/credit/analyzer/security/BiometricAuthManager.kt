package com.smart.credit.analyzer.security

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import javax.crypto.Cipher

/**
 * 生物认证管理器
 */
class BiometricAuthManager(private val context: Context) {
    
    private val executor: Executor = ContextCompat.getMainExecutor(context)
    
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
        this.callback = callback
        
        val biometricPrompt = BiometricPrompt(context, executor, 
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    callback?.onAuthenticated()
                }
                
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    callback?.onError(errString.toString())
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    callback?.onError("认证失败，请重试")
                }
            }
        )
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("生物认证解锁")
            .setSubtitle("使用指纹或面部识别访问信用数据")
            .setNegativeButtonText("取消")
            .build()
        
        val cipher = createCipher()
        if (cipher != null) {
            biometricPrompt.authenticate(promptInfo, cipher)
        } else {
            // 如果没有可用的密钥，直接认证
            callback?.onAuthenticated()
        }
    }
    
    /**
     * 检查设备是否支持生物认证
     */
    fun isBiometricAvailable(): Boolean {
        return BiometricPrompt.isHardwareDetected(context) &&
            BiometricManager.from(context).canAuthenticate(
                BiometricPrompt.BIOMETRIC_STRONG or BiometricPrompt.CREDENTIAL_STRONG
            ) == BiometricManager.BIOMETRIC_SUCCESS
    }
    
    /**
     * 创建用于认证的密码器
     */
    private fun createCipher(): Cipher? {
        try {
            val keyStore = java.security.KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            
            val key = keyStore.getKey("credit_analyzer_key", null) as? java.security.Key
                ?: return null
            
            return Cipher.getInstance("AES/ECB/PKCS7Padding").also {
                it.init(Cipher.WRAP_MODE, key)
            }
        } catch (e: Exception) {
            return null
        }
    }
}