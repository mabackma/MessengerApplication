package com.example.messengerapplication.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "my_app_database.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Luodaan chatroom_messages taulu jos sitä ei ole jo olemassa.
        db.execSQL("CREATE TABLE IF NOT EXISTS chatroom_messages (id INTEGER PRIMARY KEY, sender TEXT, timestamp TEXT, content TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Viestejä ei muokata
    }
}