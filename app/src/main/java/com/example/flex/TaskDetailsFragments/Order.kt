package com.example.flex.TaskDetailsFragments

import java.util.*

class Order {
    var ruid: String? = null
    var destination: String? = null
    var creation_Date: Date? = null
    var order_content : MutableMap<String,Int> = mutableMapOf()

    constructor(creation_date: Date?, ruid:String?, dest:String?,content:MutableMap<String,Int>) {
        this.creation_Date = creation_date
        this.ruid = ruid
        this.destination = dest
        this.order_content = content
    }
}