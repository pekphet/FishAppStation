package com.fish.fishdownloader.service

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

/**
 * Created by fish on 18-1-30.
 */
class DownloadInfoContentProvider : ContentProvider() {

    companion object {
        private const val AUTHORITY = "com.fish.download.provider"
        private const val CODE_MATCH = 0X1000
        private const val TABLE = "download"
        private val APP_URI = Uri.parse("content://$AUTHORITY/app")
        val URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "app", CODE_MATCH)
        }
    }

    lateinit var mDB: SQLiteDatabase

    override fun onCreate(): Boolean {
        mDB = DownloadDBHelper(context).writableDatabase
        return false
    }

    override fun insert(uri: Uri, values: ContentValues): Uri {
        if (URI_MATCHER.match(uri) == CODE_MATCH) {
            mDB.insert(TABLE, null, values)
            context.contentResolver.notifyChange(uri, null)
        }
        return uri
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        var deleted = 0
        if (URI_MATCHER.match(uri) == CODE_MATCH) {
            deleted = mDB.delete(TABLE, selection, selectionArgs)
            context.contentResolver.notifyChange(uri, null)
        }
        return deleted
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        var updated = 0
        if (URI_MATCHER.match(uri) == CODE_MATCH) {
            updated = mDB.update(TABLE, values, selection, selectionArgs)
            context.contentResolver.notifyChange(uri, null)
        }
        return updated
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        if (URI_MATCHER.match(uri) == CODE_MATCH) {
            return DownloadDBHelper(context).readableDatabase.query(TABLE, projection, selection, selectionArgs, null, null, sortOrder)
        }
        return null
    }

    override fun getType(uri: Uri?) = null
}

class DownloadDBHelper(val context: Context?) : SQLiteOpenHelper(context, "download.db", null, 1) {
    val CREATE_SQL = """
CREATE TABLE download
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    tag TEXT NOT NULL,
    fName TEXT NOT NULL,
    url TEXT NOT NULL,
    path TEXT,
    ptr INT DEFAULT 0 NOT NULL,
    size INT DEFAULT 0 NOT NULL,
    cancelSignal BOOL DEFAULT FALSE NOT NULL,
    pauseSignal BOOL DEFAULT FALSE  NOT NULL
);
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_SQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}