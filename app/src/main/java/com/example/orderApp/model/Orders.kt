package com.example.orderApp.model

import java.util.ArrayList

data class Orders(
    val id: String,
    val orderDate: String,
    val status: String,
    val description: String,
    val paymentMethod: String,
    val orderingMethod: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val address1: String,
    val city: String,
    val postcode: String,
    val items: List<OrderItem>,
    val orderLength:Int
    )


data class OrderItem(
    val menuItemId: ArrayList<String>,
    val menuItemName: ArrayList<String>,
    val price: ArrayList<String>,
    val quantity: ArrayList<String>,
    val totalPrice: ArrayList<String>
)
data class Orderss(
    val id: String,
    val orderDate: String,
    val status: String,
    val description: String,
    val paymentMethod: String,
    val orderingMethod: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val items: List<OrderShow>,
    val orderLength:Int
)
data class OrderShow(
    val menuItemId:String,
    val menuItemName:String,
    val price: String,
    val quantity: String,
    val totalPrice:String
)
