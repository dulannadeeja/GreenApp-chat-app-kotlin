package com.example.chatapp.controllers

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class AESEncryptor {
    private var secretKey: SecretKeySpec? = null
    private lateinit var key: ByteArray
    private lateinit var keyGenerator: KeyGenerator

    // generate a unique key
    fun generateKey(): SecretKey? {
        val keygen = KeyGenerator.getInstance("AES")
        keygen.init(256)
        val key = keygen.generateKey()
        return key
        //saveSecretKey(key)
    }

    // set the secret Key
    private fun setKey(context: Context, myKey: String) {
        var sha: MessageDigest? = null
        try {
            key = myKey.toByteArray(charset("UTF-8"))
            sha = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = Arrays.copyOf(key, 16)
            secretKey = SecretKeySpec(key, "AES")
            Log.i("TAG","$key")
            Log.i("TAG","$secretKey")
        } catch (e: NoSuchAlgorithmException) {
            Toast.makeText(context,"Error while Set The Secret Key$e", Toast.LENGTH_SHORT).show()
        } catch (e: UnsupportedEncodingException) {
            Toast.makeText(context,"Error while Set The Secret Key$e", Toast.LENGTH_SHORT).show()
        }
    }

    // method to encrypt the secret text using the secret key
    @RequiresApi(Build.VERSION_CODES.O)
    fun encrypt(context: Context, strToEncrypt: String, secret: String): String? {
        try {
            setKey(context,secret)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return Base64.getEncoder().encodeToString(cipher.doFinal
                (strToEncrypt.toByteArray(charset("UTF-8"))))

        } catch (e: Exception) {
            Toast.makeText(context,"Error while encrypting: $e", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    // method to Decrypt the secret text using the secret key
    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(context: Context, strToDecrypt: String?, secret: String): String? {
        try {
            setKey(context,secret)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return String(cipher.doFinal(
                Base64.getDecoder().
                decode(strToDecrypt)))
        } catch (e: Exception) {
            Toast.makeText(context,"Error while decrypting: $e", Toast.LENGTH_SHORT).show()
        }
        return null
    }
}