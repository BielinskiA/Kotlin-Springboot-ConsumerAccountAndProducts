package com.example.springboot.Tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ConfigServerTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val key: Column<String> = varchar("key", 50)
    val value: Column<String> = varchar("value", 100)
    val created = datetime("created").defaultExpression(CurrentDateTime)
}