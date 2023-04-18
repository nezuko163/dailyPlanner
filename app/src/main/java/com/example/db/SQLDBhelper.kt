package com.example.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.model.BusinessModel as BusinessModel

class SQLDBhelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private val DB_NAME = "business.db"
        private val DB_VERSION = 1
        private val TABLE_NAME = "buss"

        private val COLUMN_ID = "id"
        private val COLUMN_NAME_OF_BUS = "business"
        private val COLUMN_YEAR = "year"
        private val COLUMN_MONTH = "month"
        private val COLUMN_DAY = "day"
        private val COLUMN_HOUR_START = "hour_start"
        private val COLUMN_MIN_START = "min_start"
        private val COLUMN_HOUR_END = "hour_end"
        private val COLUMN_MIN_END = "min_end"
    }

    interface SQLDBhelper {
        fun deleteBusiness(id: Int)
        fun deleteBusiness(business: BusinessModel)
        fun deleteAll()
        fun redactBusiness(id: Int, business: BusinessModel)
        fun getBusinesses(condition: String = "1"): ArrayList<BusinessModel>
        fun addBusiness(business: BusinessModel): Long
        fun deleteDayTimeBusinesses(year: Int, month: Int, day: Int)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val create_db = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "$COLUMN_NAME_OF_BUS TEXT, " +
                "$COLUMN_YEAR INTEGER," +
                "$COLUMN_MONTH INTEGER, " +
                "$COLUMN_DAY INTEGER, " +
                "$COLUMN_HOUR_START INTEGER, " +
                "$COLUMN_MIN_START INTEGER, " +
                "$COLUMN_HOUR_END INTEGER, " +
                "$COLUMN_MIN_END INTEGER);"
        db?.execSQL(create_db)

        db?.close()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val drop_db = "DROP TABLE IF EXISTS $TABLE_NAME;"
        db!!.execSQL(drop_db)

        db.close()
    }

    fun deleteBusiness(id: Int) {
        val db = this.writableDatabase

        val delete_q = "DELETE FROM $TABLE_NAME WHERE id = $id;"
        db.execSQL(delete_q)
        db.close()
    }

    fun deleteBusiness(business: BusinessModel) {
        val db = this.writableDatabase

        val delete_q = "DELETE FROM $TABLE_NAME WHERE " +
                "$COLUMN_NAME_OF_BUS = \"${business.name_of_business}\" AND " +
                "$COLUMN_YEAR = ${business.year} AND " +
                "$COLUMN_MONTH = ${business.month} AND " +
                "$COLUMN_DAY = ${business.day} AND " +
                "$COLUMN_HOUR_START = ${business.hour_start} AND " +
                "$COLUMN_MIN_START = ${business.min_start} AND " +
                "$COLUMN_HOUR_END = ${business.hour_end} AND " +
                "$COLUMN_MIN_END = ${business.min_end};"
        db.execSQL(delete_q)
        db.close()
    }


    fun deleteAll() {
        val db = this.writableDatabase

        val delete_q = "DELETE FROM $TABLE_NAME;"
        db.execSQL(delete_q)
        db.close()
    }

    fun redactBusiness(business_old: BusinessModel, business_new: BusinessModel) {
        val contentValues = ContentValues()
        val db = this.writableDatabase
        contentValues.put(COLUMN_NAME_OF_BUS, business_new.name_of_business)
        contentValues.put(COLUMN_YEAR, business_new.year)
        contentValues.put(COLUMN_MONTH, business_new.month)
        contentValues.put(COLUMN_DAY, business_new.day)
        contentValues.put(COLUMN_HOUR_START, business_new.hour_start)
        contentValues.put(COLUMN_MIN_START, business_new.min_start)
        contentValues.put(COLUMN_HOUR_END, business_new.hour_end)
        contentValues.put(COLUMN_MIN_END, business_new.min_end)

        val condition = "$COLUMN_NAME_OF_BUS = \"${business_old.name_of_business}\" AND \n" +
                "$COLUMN_YEAR = ${business_old.year} AND \n" +
                "$COLUMN_MONTH = ${business_old.month} AND \n" +
                "$COLUMN_DAY = ${business_old.day} AND \n" +
                "$COLUMN_HOUR_START = ${business_old.hour_start} AND \n" +
                "$COLUMN_MIN_START = ${business_old.min_start} AND \n" +
                "$COLUMN_HOUR_END = ${business_old.hour_end} AND \n" +
                "$COLUMN_MIN_END = ${business_old.min_end}"

        db.update(TABLE_NAME, contentValues, condition, arrayOf())
        db.close()
    }

    fun deleteDayTimeBusinesses(year: Int, month: Int, day: Int) {
        val db = this.writableDatabase

        val delete_q = "DELETE FROM $TABLE_NAME WHERE " +
                "$COLUMN_YEAR = $year AND " +
                "$COLUMN_MONTH = $month AND " +
                "$COLUMN_DAY = $day;"
        db.execSQL(delete_q)
        db.close()
    }


    fun addBusiness(business: BusinessModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_NAME_OF_BUS, business.name_of_business)
        contentValues.put(COLUMN_YEAR, business.year)
        contentValues.put(COLUMN_MONTH, business.month)
        contentValues.put(COLUMN_DAY, business.day)
        contentValues.put(COLUMN_HOUR_START, business.hour_start)
        contentValues.put(COLUMN_MIN_START, business.min_start)
        contentValues.put(COLUMN_HOUR_END, business.hour_end)
        contentValues.put(COLUMN_MIN_END, business.min_end)

        val success = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return success
    }

    fun getBusinesses(condition: String = "1"): ArrayList<BusinessModel> {
        val bs_list: ArrayList<BusinessModel> = ArrayList()
        val select_q = "SELECT * FROM $TABLE_NAME where $condition;"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(select_q, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(select_q)
            return ArrayList()
        }

        var id: Int
        var business: String
        var year: Int
        var month: Int
        var day: Int
        var hour_start: Int
        var min_start: Int
        var hour_end: Int
        var min_end: Int


        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(max(0, cursor.getColumnIndex("$COLUMN_ID")))
                business = cursor.getString(max(0, cursor.getColumnIndex("$COLUMN_NAME_OF_BUS")))
                year = cursor.getInt(max(0, cursor.getColumnIndex("$COLUMN_YEAR")))
                month = cursor.getInt(max(0, cursor.getColumnIndex("$COLUMN_MONTH")))
                day = cursor.getInt(max(0, cursor.getColumnIndex("$COLUMN_DAY")))
                hour_start = cursor.getInt(max(0, cursor.getColumnIndex("$COLUMN_HOUR_START")))
                min_start = cursor.getInt(max(0, cursor.getColumnIndex("$COLUMN_MIN_START")))
                hour_end = cursor.getInt(max(0, cursor.getColumnIndex("$COLUMN_HOUR_END")))
                min_end = cursor.getInt(max(0, cursor.getColumnIndex("$COLUMN_MIN_END")))

                val bs = BusinessModel(
                    business,
                    year,
                    month,
                    day,
                    hour_start,
                    min_start,
                    hour_end,
                    min_end,
                    id
                )
                bs_list.add(bs)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return bs_list
    }


    fun max(a: Int, b: Int): Int {
        return if (a > b) a else b
    }
}

