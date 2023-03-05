package com.raywenderlich.android.awareness_food.repositories

import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.util.Log
import android.widget.FilterQueryProvider
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class AutoCompleteProvider : FilterQueryProvider {
    override fun runQuery(constraint: CharSequence): Cursor {
        // run in the background thread
        Log.d("MyLog", "runQuery constraint: $constraint")
//        if (constraint == null) {
//            return null;
//        }
        val columnNames = arrayOf(BaseColumns._ID, "name", "description")
        val c = MatrixCursor(columnNames)
        try {
            val urlString =
                "https://en.wikipedia.org/w/api.php?" + "action=opensearch&search=" + constraint +
                        "&limit=8&namespace=0&format=json"
            val url = URL(urlString)
            val stream = url.openStream()
            val reader = BufferedReader(InputStreamReader(stream))
            val jsonStr = reader.readLine()
            // output ["query", ["n0", "n1", ..], ["d0", "d1", ..]]
            val json = JSONArray(jsonStr)
            val names = json.getJSONArray(1)
            val descriptions = json.getJSONArray(2)
            for (i in 0 until names.length()) {
                c.newRow().add(i).add(names.getString(i))//.add(descriptions.getString(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return c
    }
}