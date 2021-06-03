package com.example.flex.Bag

class Bag {

    var bid: String? = null
    var creation_Date: String? = null
    var ruid: String? =  null
    var stock: MutableList<Medicine> = mutableListOf()

    constructor() {}
    constructor(creation_date:String?) {
        this.creation_Date = creation_date

    }

    fun getCreationDate(): String? {
        return creation_Date
    }

}