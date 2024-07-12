package com.example.chatapp.model

class UserData{
    var userID:String?=""
    var mobile:String? = ""
    var userName:String? = ""
    var bio:String? = null
    var profileUri: String? = null

    constructor(){}
    constructor(userID:String?,
                mobile:String?,
                userName:String?,
                bio:String?,
                profileUri: String?){
        this.userName=userName
        this.profileUri=profileUri
        this.bio=bio
        this.mobile=mobile
        this.userID=userID
    }

}


