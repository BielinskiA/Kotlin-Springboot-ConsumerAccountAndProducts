package com.example.springboot.Connects

import org.jetbrains.exposed.sql.Database

object DataBaseControl {
    val connect = Database.connect(
        "jdbc:mariadb://10.1.2.85:3306/projekt2", driver = "org.mariadb.jdbc.Driver",
        user = "root", password = "praktykanci2024"
    )
}