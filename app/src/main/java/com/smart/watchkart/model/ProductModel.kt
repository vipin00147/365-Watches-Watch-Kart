package com.smart.watchkart.model

class ProductModel {
    var id = 0
    var product_Name: String? = null
    var brand_Name: String? = null
    var serial_Number: String? = null
    private var _detail: String? = null
    private var _price = 0
    var product_Image: String? = null
    private var _quantity = 0

    constructor() {}

    fun get_detail(): String? {
        return _detail
    }

    fun set_detail(_detail: String?) {
        this._detail = _detail
    }

    fun get_price(): Int {
        return _price
    }

    fun set_price(_price: Int) {
        this._price = _price
    }

    fun get_quantity(): Int {
        return _quantity
    }

    fun set_quantity(_quantity: Int) {
        this._quantity = _quantity
    }

    fun get_is_AddedTo_Cart(): Boolean {
        return is_AddedTo_Cart
    }

    fun set_is_AddedTo_Cart(is_addedTo_cart: Boolean) {
        this.is_AddedTo_Cart = is_addedTo_cart
    }

    var is_AddedTo_Cart: Boolean = false

    constructor(
        id: Int,
        product_name: String?,
        brand_name: String?,
        serial_number: String?,
        detail: String?,
        price: Int,
        product_image: String?,
        quantity: Int,
        is_addedTo_cart: Boolean
    ) {
        this.id = id
        product_Name = product_name
        brand_Name = brand_name
        serial_Number = serial_number
        _detail = detail
        _price = price
        product_Image = product_image
        _quantity = quantity
        is_AddedTo_Cart = is_addedTo_cart
    }
}