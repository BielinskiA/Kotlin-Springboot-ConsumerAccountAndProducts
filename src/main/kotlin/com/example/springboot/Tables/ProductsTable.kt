package com.example.springboot.Tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ProductsTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 50)
    val code: Column<String> = varchar("code", 50)
    val ean: Column<String> = varchar("ean", 100)
    val taxes: Column<String> = varchar("taxes", 50)
    val netto: Column<String> = varchar("netto", 50)
    val added = datetime("added").defaultExpression(CurrentDateTime)
}