package com.example.springboot.DataClasses

import kotlinx.datetime.LocalDateTime

data class AdminsData(
    val id: Int,
    val name: String,
    val surname: String,
    val password: String,
    val email: String,
    val phoneNumber: String,
    val created: LocalDateTime
)

data class NewAdminsData(
    val name: String,
    val surname: String,
    val password: String,
    val email: String,
    val phoneNumber: String
)
