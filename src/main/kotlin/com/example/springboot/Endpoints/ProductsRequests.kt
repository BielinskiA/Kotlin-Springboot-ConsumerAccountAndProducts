package com.example.springboot.Endpoints

import com.example.springboot.Connects.DataBaseControl
import com.example.springboot.DataClasses.*
import com.example.springboot.JWT.HeaderAuthenticationJwtFilter
import com.example.springboot.Managers.AdminManager
import com.example.springboot.Managers.ProductsManager
import com.example.springboot.Tables.AdminsTable
import com.example.springboot.Tables.ProductsTable
import com.sun.jna.platform.win32.Sspi.TimeStamp
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File

@RestController
class ProductsRequests {
    @GetMapping("/products")
    fun getProduct(
        @RequestAttribute(name = HeaderAuthenticationJwtFilter.USER) admin: AdminsData,
        @RequestParam(required = false) id: Int?,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) code: String?,
        @RequestParam(required = false) ean: String?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): Any? {
        return transaction(DataBaseControl.connect) {
            val productsAll = ProductsTable.selectAll()
            id?.let {
                productsAll.where(ProductsTable.id.eq(id))
            }
            name?.let {
                productsAll.where(ProductsTable.name.eq(name))
            }
            code?.let {
                productsAll.where(ProductsTable.code.eq(code))
            }
            ean?.let {
                productsAll.where(ProductsTable.ean.eq(ean))
            }

            val totalProducts = productsAll.count()
            val products = productsAll
                .limit(size, offset = (page * size).toLong())
                .map {
                    ProductsData(
                        it[ProductsTable.id],
                        it[ProductsTable.name],
                        it[ProductsTable.code],
                        it[ProductsTable.ean],
                        it[ProductsTable.taxes],
                        it[ProductsTable.netto],
                        it[ProductsTable.added]
                    )
                }

            if (id != null && products.isEmpty()) {
                return@transaction ResponseEntity(MessageData("Nie ma takiego użytkownika"), HttpStatus.BAD_REQUEST)
            }
            if (name != null && products.isEmpty()) {
                return@transaction ResponseEntity(MessageData("Nie ma takiego użytkownika"), HttpStatus.BAD_REQUEST)
            }
            if (code != null && products.isEmpty()) {
                return@transaction ResponseEntity(MessageData("Nie ma takiego użytkownika"), HttpStatus.BAD_REQUEST)
            }
            if (ean != null && products.isEmpty()) {
                return@transaction ResponseEntity(MessageData("Nie ma takiego użytkownika"), HttpStatus.BAD_REQUEST)
            }
            val pageImpl = PageImpl(products, PageRequest.of(page, size), totalProducts)
            ResponseEntity(pageImpl, HttpStatus.OK)
        }
    }
    @PostMapping("/products")
    fun addProducts(
        @RequestAttribute(name = HeaderAuthenticationJwtFilter.USER) admin: AdminsData,
        @RequestBody product: NewProductsData
    ): ResponseEntity<*> {
        if (ProductsManager().codeExist(product.code)){
            return ResponseEntity(MessageData("Przedmiot o takim kodzie już istnieje"), HttpStatus.BAD_REQUEST)
        }
        if (ProductsManager().eanExist(product.ean)){
            return ResponseEntity(MessageData("Przedmiot o takim kodzie już istnieje"), HttpStatus.BAD_REQUEST)
        }
        val productId = transaction (DataBaseControl.connect){
            ProductsTable.insert {
                it[name] = product.name
                it[code] = product.code
                it[ean] = product.ean
                it[taxes] = product.taxes
                it[netto] = product.netto
            }[ProductsTable.id]
        }
        return ResponseEntity(productId, HttpStatus.CREATED)
    }
    @PostMapping("/products/{id}")
    fun updateProducts(
        @RequestAttribute(name = HeaderAuthenticationJwtFilter.USER) admin: AdminsData,
        @RequestBody product: UpdateProductsData,
        @PathVariable id: Int
    ): ResponseEntity<MessageData> {
        if (!ProductsManager().productId(id)) {
            return ResponseEntity(MessageData("Nie ma produktu o takim ID"), HttpStatus.BAD_REQUEST)
        }
        transaction(DataBaseControl.connect) {
            ProductsTable.update({ ProductsTable.id eq id }) {
                it[ProductsTable.id] = id
                it[name] = product.name
                it[code] = product.code
                it[ean] = product.ean
                it[taxes] = product.taxes
                it[netto] = product.netto
            }
        }
        return ResponseEntity(MessageData("Udało się zaktualizować produkt"), HttpStatus.OK)
    }

    @PostMapping("/import")
    fun importProductsFromCsv(@RequestParam("file") file: MultipartFile): ResponseEntity<*> {
        return try {
            val tempFile = File.createTempFile("temp", ".csv")
            file.transferTo(tempFile)

            val importedProducts = ProductsManager().importProductsFromCsv(tempFile.absolutePath)

            ResponseEntity(importedProducts, HttpStatus.CREATED)
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity("Błąd w importowaniu pliku: ${e.message}", HttpStatus.BAD_REQUEST)
        }
    }
    @DeleteMapping("/products")
    fun deleteProducts(
        @RequestAttribute(name = HeaderAuthenticationJwtFilter.USER) user: AdminsData,
        @RequestParam id: Int
    ): Any {
        if (!ProductsManager().productId(id)){
            return ResponseEntity(MessageData("Nie ma produktu o takim ID"), HttpStatus.BAD_REQUEST)
        }
        transaction(DataBaseControl.connect){
            ProductsTable.deleteWhere { ProductsTable.id.eq(id) }
        }
        return ResponseEntity(MessageData("Udało się usunąć przedmiot"), HttpStatus.ACCEPTED)
    }
}