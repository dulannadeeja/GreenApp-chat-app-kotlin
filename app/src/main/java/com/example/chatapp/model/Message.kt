package com.example.chatapp.model

class Message {
    var messageID: String? = null
    var dateTime: String? = null
    var duration: String? = null
    var type: String? = null
    var messageContentText: String? = null
    var messageContentImageUri: String? = null
    var messageContentVoice: String? = null
    var messageContentAttachment:String?=null
    var senderID: String? = null
    var receiverID: String? = null

    constructor() {}
    constructor(
        messageID: String?,
        duration: String?,
        dateTime: String?,
        type: String?,
        messageContentText: String?,
        messageContentImageUri: String?,
        messageContentVoice:String?,
        messageContentAttachment:String?,
        senderID: String?,
        receiverID: String?
    ) {

        this.dateTime = dateTime
        this.duration = duration
        this.type = type
        this.messageID = messageID
        this.messageContentText = messageContentText
        this.messageContentImageUri = messageContentImageUri
        this.messageContentVoice=messageContentVoice
        this.messageContentAttachment=messageContentAttachment
        this.receiverID = receiverID
        this.senderID = senderID
    }
}