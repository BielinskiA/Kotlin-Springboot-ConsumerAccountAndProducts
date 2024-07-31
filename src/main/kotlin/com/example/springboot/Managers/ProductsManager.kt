package com.example.springboot.Managers

import com.example.springboot.Connects.DataBaseControl
import com.example.springboot.DataClasses.NewProductsData
import com.example.springboot.Tables.ProductsTable
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.io.File

@Service
class ProductsManager {
    fun productExist(id: Int):Boolean{
        val x = transaction(DataBaseControl.connect) {
            ProductsTable.selectAll().where(ProductsTable.id.eq(id)).firstOrNull()
        }
        return x != null
    }
    fun productId(id: Int): Boolean{
        val c = transaction(DataBaseControl.connect) {
            ProductsTable.selectAll().where(ProductsTable.id.eq(id)).map {
                it[ProductsTable.id]
            }.firstOrNull()
        }
        return c != null
    }
    fun codeExist(code: String): Boolean{
        val a = transaction (DataBaseControl.connect){
            ProductsTable.selectAll().where(ProductsTable.code.eq(code)).map{
                it[ProductsTable.code]
            }.firstOrNull()
        }
        return a != null
    }
    fun eanExist(ean: String): Boolean{
        val b = transaction (DataBaseControl.connect){
            ProductsTable.selectAll().where(ProductsTable.ean.eq(ean)).map{
                it[ProductsTable.ean]
            }.firstOrNull()
        }
        return b != null
    }
    fun importProductsFromCsv(filePath: String): List<NewProductsData> {
        val csvMapper = CsvMapper()
        val schema = CsvSchema.emptySchema().withHeader()
        val file = File(filePath)

        val products: List<NewProductsData> = csvMapper.readerFor(NewProductsData::class.java)
            .with(schema)
            .readValues<NewProductsData>(file)
            .readAll()

        val importedProducts = mutableListOf<NewProductsData>()

        transaction (DataBaseControl.connect){
            products.forEach { product ->
                if (!ProductsManager().codeExist(product.code) && !ProductsManager().eanExist(product.ean)) {
                    ProductsTable.insert { row ->
                        row[ProductsTable.name] = product.name
                        row[ProductsTable.code] = product.code
                        row[ProductsTable.ean] = product.ean
                        row[ProductsTable.taxes] = product.taxes
                        row[ProductsTable.netto] = product.netto
                    }
                    importedProducts.add(product)
                }
            }
        }
        return importedProducts
    }
}