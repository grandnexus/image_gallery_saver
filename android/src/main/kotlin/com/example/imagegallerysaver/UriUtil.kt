package com.example.imagegallerysaver

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils

object UriUtil {
    fun convertUriToPath(context: Context, uri: Uri?): String? {
        if (uri == null) return null
        val schema = uri.scheme
        if (TextUtils.isEmpty(schema) || ContentResolver.SCHEME_FILE == schema) {
            return uri.path
        }
        if ("http" == schema) return uri.toString()
        if (ContentResolver.SCHEME_CONTENT == schema) {
            val projection = arrayOf(MediaStore.MediaColumns.DATA)
            var cursor: Cursor? = null
            var filePath: String? = ""
            try {
                cursor = context.contentResolver.query(uri, projection, null, null, null)
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        filePath = cursor.getString(0)
                    }
                    cursor.close()
                }
            } catch (e: Exception) {
                // do nothing
            } finally {
                try {
                    cursor?.close()
                } catch (e2: Exception) {
                    // do nothing
                }
            }
            if (TextUtils.isEmpty(filePath)) {
                try {
                    val contentResolver = context.contentResolver
                    val selection = MediaStore.Images.Media._ID + "= ?"
                    var id = uri.lastPathSegment
                    if (Build.VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(id) && id!!.contains(":")) {
                        id = id.split(":").toTypedArray()[1]
                    }
                    val selectionArgs = arrayOf(id)
                    cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null)
                    if (cursor!!.moveToFirst()) {
                        filePath = cursor.getString(0)
                    }
                    if (null != cursor) {
                        cursor.close()
                    }
                } catch (e: Exception) {
                    // do nothing
                } finally {
                    try {
                        cursor?.close()
                    } catch (e: Exception) {
                        // do nothing
                    }
                }
            }
            return filePath
        }
        return null
    }

    fun getImagePath(context: Context, uri: Uri?, selection: String?): String? {
        var path: String? = null
        val cursor = context.contentResolver.query(uri!!, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }
}