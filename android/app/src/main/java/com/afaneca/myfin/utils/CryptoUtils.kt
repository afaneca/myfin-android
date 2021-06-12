package com.afaneca.myfin.utils


import com.tozny.crypto.android.AesCbcWithIntegrity
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import javax.crypto.*

/**
 * Created by me on 11/06/2021
 */
class CryptoUtils {
    companion object {
        fun encryptWithAES(keyStr: String, plainText: String): String {
            if (keyStr.isNullOrBlank()) return ""

            val secretKey = AesCbcWithIntegrity.keys(keyStr)
            val cIv = AesCbcWithIntegrity.encrypt(plainText, secretKey)
            return cIv.toString()
        }

        fun decryptWithAES(keyStr: String, encryptedText: String): String {
            try{
                val secretKey = AesCbcWithIntegrity.keys(keyStr)
                val cIv = AesCbcWithIntegrity.CipherTextIvMac(encryptedText)
                return AesCbcWithIntegrity.decryptString(cIv, secretKey)
            }catch (e: Exception) {
                e.printStackTrace();
                return "";
            }
        }
    }

}