package com.example.messengerapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase


class MessageDAO(context: Context?) {
    private var database: SQLiteDatabase? = null

    init {
        val dbHelper = context?.let { MyDatabaseHelper(it) }
        if (dbHelper != null) {
            database = dbHelper.getWritableDatabase()
        }
    }

    // Viestin lisääminen tietokantaan
    fun insertMessage(message: Message) {
        // Avain-arvo parit viestien tallentamista varten.
        val values = ContentValues().apply {
            put("timestamp", message.timestamp)
            put("content", message.content)
        }
        database?.insert("chatroom_messages", null, values)
    }

    // Viestien lukeminen tietokannasta
    fun getAllMessages(): List<Message> {
        val messages = mutableListOf<Message>()

        // chatroom_messages taulun rakenne.
        val columns = arrayOf("id", "timestamp", "content")
        val orderBy = "timestamp ASC"  // järjestys vanhimmasta uusimpaan viestiin

        // Kursori kyselyä varten
        // Kysely hakee kaikki viestit chatroom_messages taulusta ja ainoana kyselyparametrina on järjestys
        val cursor = database?.query("chatroom_messages", columns, null, null, null, null, orderBy)

        // Kursori käy kaikki viestit tietokannassa läpi ja lisää ne messages listaan
        cursor?.use {
            while (it.moveToNext()) {
                val messageId = it.getInt(it.getColumnIndex("id"))
                val timestamp = it.getString(it.getColumnIndex("timestamp"))
                val content = it.getString(it.getColumnIndex("content"))

                val message = Message(messageId, timestamp, content)
                messages.add(message)
            }
        }

        // Palautetaan messages lista.
        return messages
    }

    fun deleteAllMessages() {
        database?.delete("chatroom_messages", null, null)
    }
}