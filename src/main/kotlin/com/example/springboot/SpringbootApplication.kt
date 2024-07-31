package com.example.springboot

import com.example.springboot.Tables.AdminsTable
import com.example.springboot.Tables.ConfigServerTable
import com.example.springboot.Tables.ProductsTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringbootApplication

fun main(args: Array<String>) {
	runApplication<SpringbootApplication>(*args)
}
