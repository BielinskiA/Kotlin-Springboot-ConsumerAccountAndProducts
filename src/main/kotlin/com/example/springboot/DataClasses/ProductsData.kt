package com.example.springboot.DataClasses

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.datetime.LocalDateTime

data class ProductsData(
    val id: Int,
    val name: String,
    val code: String,
    val ean: String,
    val taxes: String,
    val netto: String,
    val added: LocalDateTime
)

data class NewProductsData @JsonCreator constructor(
    @JsonProperty("name") val name: String,
    @JsonProperty("code") val code: String,
    @JsonProperty("ean") val ean: String,
    @JsonProperty("taxes") val taxes: String,
    @JsonProperty("netto") val netto: String
)

data class UpdateProductsData(
    val id: Int,
    val name: String,
    val code: String,
    val ean: String,
    val taxes: String,
    val netto: String
)