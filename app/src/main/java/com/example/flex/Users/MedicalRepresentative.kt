package com.example.flex.Users

//change name
class MedicalRepresentative {
    var name: String? = null
    var surname: String? = null
    var phoneno: String? = null
    var address: String? = null
    var birthdate: String? = null
    var isAdmin: String? = null

    constructor() {}
    constructor(name: String?, surname: String?, phoneno: String?, address: String?, birthdate: String?) {
        this.name = name
        this.surname = surname
        this.phoneno = phoneno
        this.address = address
        this.birthdate = birthdate
    }

    fun getUserName(): String? {
        return name
    }

    fun getUserSurname(): String? {
        return surname
    }

    fun getUserPhoneno(): String? {
        return phoneno
    }
    fun getUserAddress(): String? {
        return address
    }

    fun getUserBirth(): String? {
        return birthdate
    }

    fun getUserisAdmin(): String? {
        return isAdmin
    }
}
/*data class MedicalRepresentative (
    var name: String = "",
    var surname: String = "",
    var Phone_num: String = ""
   // var BirthDate: String = "",
    //var address: String = ""

// address
// alternative address
)
{
   // var uid: String = ""
    //var bag: Bag? = null
    //var email: String = ""
    //var user_status: String = ""

}
*/