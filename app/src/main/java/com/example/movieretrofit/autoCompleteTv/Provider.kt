package com.example.movieretrofit.autoCompleteTv

import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.widget.FilterQueryProvider
import com.example.movieretrofit.MainActivity

class Provider : FilterQueryProvider {
    override fun runQuery(constraint: CharSequence): Cursor {
        val columnNames = arrayOf(BaseColumns._ID, "name")
        val cursor = MatrixCursor(columnNames)
        try {
            val allFood = MainActivity().getAllFood(constraint.toString())

//            val names = allFood!!.searchResults[5].results
//            for (i in names.indices) {
//                cursor.newRow().add(i).add(names[i].name)
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cursor
    }
}
