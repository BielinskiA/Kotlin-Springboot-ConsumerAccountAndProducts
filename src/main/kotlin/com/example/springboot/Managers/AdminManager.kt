package com.example.springboot.Managers

import com.example.springboot.Connects.DataBaseControl
import com.example.springboot.DataClasses.AdminsData
import com.example.springboot.Tables.AdminsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.security.MessageDigest

@Service
class AdminManager {
    fun getAdminById(id: Int): List<AdminsData> {
        return transaction(DataBaseControl.connect) {
            AdminsTable.selectAll().where(AdminsTable.id.eq(id)).map {
                AdminsData(
                    it[AdminsTable.id],
                    it[AdminsTable.name],
                    it[AdminsTable.surname],
                    it[AdminsTable.password],
                    it[AdminsTable.email],
                    it[AdminsTable.phoneNumber],
                    it[AdminsTable.created]
                )
            }
        }
    }
    fun adminExist(id: Int): Boolean {
        val ok = transaction(DataBaseControl.connect) {
            AdminsTable.selectAll().where(AdminsTable.id.eq(id)).map {
                it[AdminsTable.id]
            }.firstOrNull()
        }
        return (ok != null)
    }
    fun findAdminByEmail(email: String): AdminsData? {
        return transaction(DataBaseControl.connect) {
            AdminsTable.selectAll().where(AdminsTable.email.eq(email)).map {
                AdminsData(
                    it[AdminsTable.id],
                    it[AdminsTable.name],
                    it[AdminsTable.surname],
                    it[AdminsTable.email],
                    it[AdminsTable.password],
                    it[AdminsTable.phoneNumber],
                    it[AdminsTable.created]
                )
            }
                .firstOrNull()
        }
    }
    fun emailExist(maill: String): Boolean {
        val adminEmail = transaction (DataBaseControl.connect) {
            AdminsTable.selectAll().where(AdminsTable.email.eq(maill)).map {
                it[AdminsTable.email]
            }.firstOrNull()
        }
        return (adminEmail != null)
    }
    fun isEmailValid(emaill: String): Boolean {
        val emailPattern1 = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z]+"
        val emailPattern2 = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z0-9]+\\.+[a-z]+"
        return emaill.matches(emailPattern1.toRegex()) or emaill.matches(emailPattern2.toRegex())
    }
    fun hash(input: String): String {
        val bytes = input.toByteArray()
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(bytes)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    fun isPasswordStrong(passwordd: String): Boolean {
        val passwordPattern =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+$).{8,}$"
        return passwordd.matches(passwordPattern.toRegex())
    }
    fun passwordExist(password: String): Boolean{
        val hash = hash(password)
        val exists = transaction (DataBaseControl.connect){
            AdminsTable.selectAll().where(AdminsTable.password.eq(hash)).map{
                it[AdminsTable.password]
            }.firstOrNull()
        }
        return exists != null
    }
}