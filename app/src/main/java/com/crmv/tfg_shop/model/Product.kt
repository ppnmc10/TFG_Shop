package com.crmv.tfg_shop.model

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val images: List<String>
)

suspend fun fetchProducts(): List<Product> {
    val firestore = FirebaseFirestore.getInstance()
    val productsCollection = firestore.collection("products")
    val products = mutableListOf<Product>()

    try {
        val result = productsCollection.get().await()
        for (document in result.documents) {
            val id = document.id
            val name = document.getString("name") ?: ""
            val description = document.getString("description") ?: ""
            val price = document.getString("price") ?: ""
            val images = document.get("images") as? List<String> ?: emptyList()

            products.add(Product(id, name, description, price, images))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return products
}
