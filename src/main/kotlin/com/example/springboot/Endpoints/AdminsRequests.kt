package com.example.springboot.Endpoints

import com.example.springboot.Connects.DataBaseControl
import com.example.springboot.DataClasses.AdminsData
import com.example.springboot.DataClasses.LoginData
import com.example.springboot.DataClasses.MessageData
import com.example.springboot.DataClasses.NewAdminsData
import com.example.springboot.JWT.HeaderAuthenticationJwtFilter
import com.example.springboot.JWT.JWTController
import com.example.springboot.Managers.AdminManager
import com.example.springboot.Tables.AdminsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AdminsRequests {
    @GetMapping("/admins")
    fun getAdmin(
        @RequestAttribute(name = HeaderAuthenticationJwtFilter.USER) admin: AdminsData,
        @RequestParam(required = false) id: Int?,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) surname: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): Any? {
        return transaction(DataBaseControl.connect) {
            val adminAll = AdminsTable.selectAll()
            id?.let {
                adminAll.where(AdminsTable.id.eq(id))
            }
            name?.let {
                adminAll.where(AdminsTable.name.eq(name))
            }
            surname?.let {
                adminAll.where(AdminsTable.surname.eq(surname))
            }
            email?.let {
                adminAll.where(AdminsTable.email.eq(email))
            }

            val totalAdmins = adminAll.count()
            val admins = adminAll
                .limit(size, offset = (page * size).toLong())
                .map {
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

            if (id != null && admins.isEmpty()) {
                return@transaction ResponseEntity(MessageData("Nie ma takiego użytkownika"), HttpStatus.BAD_REQUEST)
            }
            if (name != null && admins.isEmpty()) {
                return@transaction ResponseEntity(MessageData("Nie ma takiego użytkownika"), HttpStatus.BAD_REQUEST)
            }
            if (surname != null && admins.isEmpty()) {
                return@transaction ResponseEntity(MessageData("Nie ma takiego użytkownika"), HttpStatus.BAD_REQUEST)
            }
            if (email != null && admins.isEmpty()) {
                return@transaction ResponseEntity(MessageData("Nie ma takiego użytkownika"), HttpStatus.BAD_REQUEST)
            }
            val pageImpl = PageImpl(admins, PageRequest.of(page, size), totalAdmins)
            ResponseEntity(pageImpl, HttpStatus.OK)
        }
    }
    @PostMapping("/admins")
    fun addAdmin(@RequestAttribute(name = HeaderAuthenticationJwtFilter.USER) admin: NewAdminsData,): ResponseEntity<*> {
        if (AdminManager().emailExist(admin.email)){
            return ResponseEntity(MessageData("Konto z takim emailem już istnieje"), HttpStatus.BAD_REQUEST)
        }
        if (AdminManager().isEmailValid(admin.email)){
            return ResponseEntity(MessageData("Email jest nieprawidłowy"), HttpStatus.BAD_REQUEST)
        }

        val adminId = transaction (DataBaseControl.connect){
            val hash = AdminManager().hash(admin.password)
            AdminsTable.insert {
                it[name] = admin.name
                it[surname] = admin.surname
                it[password] = hash
                it[email] = admin.email
                it[phoneNumber] = admin.phoneNumber
            }[AdminsTable.id]
        }
        return ResponseEntity(adminId, HttpStatus.CREATED)
    }
    @PostMapping("/login")
    fun login(@RequestBody admin: LoginData): ResponseEntity<*> {
        val adminData = transaction(DataBaseControl.connect) {
            AdminManager().findAdminByEmail(admin.email)
        }
        adminData?.let {
            if (AdminManager().passwordExist(admin.password)) {
                val token = JWTController().generateJwt(it.id)
                return ResponseEntity.ok().header("Authorization", "Bearer $token")
                    .body(MessageData("Zalogowano prawidłowo"))
            }
        }
        return ResponseEntity(MessageData("Niepoprawne hasło lub email"), HttpStatus.UNAUTHORIZED)
    }
    @DeleteMapping
    fun deleteAdmin(
        @RequestAttribute(name = HeaderAuthenticationJwtFilter.USER) admin: AdminsData,
        @RequestParam id: Int
    ): Any {
        transaction(DataBaseControl.connect){
            AdminsTable.deleteWhere { AdminsTable.id.eq(id) }
        }
        return ResponseEntity(MessageData("Udało się usunąć użytkownika"), HttpStatus.ACCEPTED)
    }
}