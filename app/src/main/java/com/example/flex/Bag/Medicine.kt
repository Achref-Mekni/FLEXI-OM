package com.example.flex.Bag

class Medicine {
    var id: String? = null
    //var creation_Date : String? =  null
    var med_name: String? = null
    var quantity: Int = 0
    var icon: Int? = 0
    //var provider: String? = null
    // fournisseur

    constructor() {}
    constructor(med_name: String?, quantity: Int) {

        this.med_name = med_name
        this.quantity = quantity
        //this.provider = provider
    }


    fun getMedName(): String? {
        return med_name
    }

    fun getMedQuantity(): Int {
        return quantity
    }


}