package com.example.springboot.Tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object AdminsTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 50)
    val surname: Column<String> = varchar("surname", 50)
    val password: Column<String> = varchar("password", 100)
    val email: Column<String> = varchar("email", 100)
    val phoneNumber: Column<String> = varchar("phone_number", 100)
    val created = datetime("created").defaultExpression(CurrentDateTime)
}