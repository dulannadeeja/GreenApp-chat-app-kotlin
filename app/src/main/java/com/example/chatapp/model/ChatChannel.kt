package com.example.chatapp.model

class ChatChannel {

    var roomID: String? = null
    var lastMessage: String? = null
    var members: ArrayList<String>? = ArrayList()

    constructor() {}
    constructor(
        roomID: String?,
        lastMessage: String?,
        members: ArrayList<String>?
    ) {
        this.roomID = roomID
        this.lastMessage = lastMessage
        this.members = members
    }
}