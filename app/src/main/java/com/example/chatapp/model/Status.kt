package com.example.chatapp.model

class Status{
    var userID:String?=null
    var imageUri: String? =null
    var dateTime: String? =null

    constructor(){}
    constructor(userID:String,imageUri:String,dateTime:String){
        this.imageUri=imageUri
        this.dateTime=dateTime
        this.userID=userID
    }
}