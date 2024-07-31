package com.example.springboot.JWT

import com.example.springboot.Connects.DataBaseControl
import com.example.springboot.Tables.ConfigServerTable
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JWTController {
    private val privateKey: SecretKey = getPrivateKey()
    private val prefix = "Bearer "

    fun generateJwt(id: Int): String {
        val now = Date()
        return Jwts.builder()
            .setSubject(id.toString())
            .setIssuedAt(now)
            .setExpiration(Date(now.time + 3600000))
            .signWith(privateKey)
            .compact()
    }

    fun verifyJwt(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(privateKey)
                .build()
                .parseClaimsJws(token.removePrefix(prefix).trim())
            true
        } catch (e: Exception) {
            false
        }
    }

    fun verifyJwtById(token: String): Int {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(privateKey)
            .build()
            .parseClaimsJws(token.removePrefix(prefix).trim())
            .body
        return (claims["sub"] as String).toInt()
    }

    private fun getPrivateKey(): SecretKey {
        return transaction(DataBaseControl.connect) {
            val configPrivateKey = ConfigServerTable.selectAll().firstOrNull()?.get(ConfigServerTable.value)
            if (configPrivateKey == null) {
                val key = Keys.secretKeyFor(SignatureAlgorithm.HS512)
                val secretKeyString = Base64.getEncoder().encodeToString(key.encoded)
                ConfigServerTable.insert {
                    it[ConfigServerTable.key] = "Klucz_prywatny"
                    it[value] = secretKeyString
                }
                key
            } else {
                val decodedKey = Base64.getDecoder().decode(configPrivateKey)
                Keys.hmacShaKeyFor(decodedKey)
            }
        }
    }
}