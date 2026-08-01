package com.smart.credit.analyzer.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * AES-GCM 加密工具类
 * 使用 Android Keystore 安全存储密钥
 */
class CryptoUtils {
    
    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "credit_analyzer_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_IV_LENGTH = 12 // 96-bit IV for GCM
        private const val GCM_TAG_LENGTH = 128 // 128-bit auth tag
        
        private val keyStore: KeyStore by lazy {
            KeyStore.getInstance(KEYSTORE_PROVIDER).also {
                it.load(null)
            }
        }
        
        /**
         * 生成或获取密钥
         */
        @Suppress("DEPRECATION")
        private fun getOrCreateKey(): SecretKey {
            if (keyStore.containsAlias(KEY_ALIAS)) {
                return keyStore.getKey(KEY_ALIAS, null) as? SecretKey
                    ?: throw Exception("Failed to get existing key")
            }
            
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, 
                KEYSTORE_PROVIDER
            )
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
            return keyGenerator.generateKey()
        }
        
        /**
         * 加密数据
         * @param plainText 明文
         * @return Base64编码的密文 (IV + ciphertext)
         */
        fun encrypt(plainText: String): String {
            try {
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
                
                val iv = cipher.iv
                val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
                
                // 组合 IV + 密文
                val combined = ByteArray(iv.size + encryptedBytes.size)
                System.arraycopy(iv, 0, combined, 0, iv.size)
                System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
                
                return android.util.Base64.encodeToString(combined, android.util.Base64.DEFAULT)
            } catch (e: Exception) {
                throw Exception("Encryption failed: ${e.message}", e)
            }
        }
        
        /**
         * 解密数据
         * @param encryptedText Base64编码的密文
         * @return 明文
         */
        fun decrypt(encryptedText: String): String {
            try {
                val decodedBytes = android.util.Base64.decode(encryptedText, android.util.Base64.DEFAULT)
                
                // 分割 IV 和密文
                val iv = ByteArray(GCM_IV_LENGTH)
                val encryptedData = ByteArray(decodedBytes.size - GCM_IV_LENGTH)
                System.arraycopy(decodedBytes, 0, iv, 0, GCM_IV_LENGTH)
                System.arraycopy(decodedBytes, GCM_IV_LENGTH, encryptedData, 0, encryptedData.size)
                
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(
                    Cipher.DECRYPT_MODE, 
                    getOrCreateKey(), 
                    GCMParameterSpec(GCM_TAG_LENGTH, iv)
                )
                
                val decryptedBytes = cipher.doFinal(encryptedData)
                return String(decryptedBytes, Charsets.UTF_8)
            } catch (e: Exception) {
                throw Exception("Decryption failed: ${e.message}", e)
            }
        }
    }
}
