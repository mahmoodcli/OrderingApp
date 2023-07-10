package com.example.orderApp.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.orderApp.model.DbModel
import java.util.LinkedList

class DbHistory(context: Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    override fun onCreate(p0: SQLiteDatabase?) {
       p0?.execSQL(" CREATE TABLE "+ TABLE_NAME+" ("+
               COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
               COLUMN_TITLE + " TEXT NOT NULL,"+
               COLUMN_EMAIL + " TEXT NOT NULL,"+
               COLUMN_PHONE + " TEXT NOT NULL,"+
               COLUMN_ITEM + " TEXT NOT NULL,"+
               COLUMN_STATUS + " TEXT NOT NULL,"+
               COLUMN_PRICE + " TEXT NOT NULL,"+
               COLUMN_DESC + " TEXT NOT NULL);"
       )

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(p0)
    }

    fun saveData(dbModel: DbModel){
        var db=this.writableDatabase
        var values=ContentValues()
        values.put(COLUMN_TITLE,dbModel.title)
        values.put(COLUMN_DESC,dbModel.desc)
        values.put(COLUMN_EMAIL,dbModel.email)
        values.put(COLUMN_PHONE,dbModel.phone)
        values.put(COLUMN_STATUS,dbModel.status)
        values.put(COLUMN_ITEM,dbModel.item)
        values.put(COLUMN_PRICE,dbModel.price)
        db.insert(TABLE_NAME,null,values)
        db.close()
    }

 @SuppressLint("Range")
 fun dataList(filter:String):MutableList<DbModel>{
     val query:String
     query= if(filter==""){
         "SELECT * FROM "+ TABLE_NAME
     } else {
         "SELECT * FROM "+ TABLE_NAME+"ORDER * BY "+filter
     }
     var dataLinkedList:MutableList<DbModel> =LinkedList()
     val db=writableDatabase
     val cursor=db.rawQuery(query,null)
     var dbModel:DbModel
     if (cursor.moveToFirst()){
         do {
             dbModel= DbModel()
             dbModel.id=cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
             dbModel.title=cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
             dbModel.desc=cursor.getString(cursor.getColumnIndex(COLUMN_DESC))

             dbModel.email=cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
             dbModel.phone=cursor.getString(cursor.getColumnIndex(COLUMN_PHONE))
             dbModel.status=cursor.getString(cursor.getColumnIndex(COLUMN_STATUS))
             dbModel.item=cursor.getString(cursor.getColumnIndex(COLUMN_ITEM))
             dbModel.price=cursor.getString(cursor.getColumnIndex(COLUMN_PRICE))
             dataLinkedList.add(dbModel)
         }while (cursor.moveToNext())
     }
     return dataLinkedList
 }

    fun deleteData(id:Long,context: Context){
        var db=this.writableDatabase
        db.execSQL("DELETE FROM "+ TABLE_NAME +" WHERE _id='"+id+"'")
    }
    fun deleteData(context: Context){
        var db=this.writableDatabase
        db.execSQL("DELETE FROM "+ TABLE_NAME)
    }
    companion object{
        private const val DATABASE_NAME="accept.db"
        private const val DATABASE_VERSION=1
        private const val TABLE_NAME="orders"
        private const val COLUMN_ID="_id"
        private const val COLUMN_TITLE="title"
        private const val COLUMN_DESC="desc"
        private const val COLUMN_EMAIL="email"
        private const val COLUMN_PHONE="phone"
        private const val COLUMN_STATUS="status"
        private const val COLUMN_ITEM="item"
        private const val COLUMN_PRICE="price"
    }
}