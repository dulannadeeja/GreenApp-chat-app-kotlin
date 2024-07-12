package com.example.chatapp.controllers

import android.content.ContentResolver
import android.provider.ContactsContract

class ContactManager {

    fun getContacts(contentResolver: ContentResolver): MutableList<String> {
        val contactList: MutableList<String> = ArrayList()
        val contacts = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        while (contacts!!.moveToNext()) {
            val number = contacts.getString(
                contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER).toInt()
            )
            val fixedNumber=number.replace("\\s".toRegex(), "")
            contactList.add(fixedNumber)
        }
        contacts.close()
        return contactList
    }
}