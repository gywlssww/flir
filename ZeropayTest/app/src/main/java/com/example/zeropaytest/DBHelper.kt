package com.example.zeropaytest

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log

class DBHelper(context: Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION){


    val wdb=writableDatabase
    val rdb=readableDatabase

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        //DB 수정이 필요한 경우
        db.execSQL("DROP TABLE IF EXISTS '${users.TABLE_NAME}'")
        db.execSQL("DROP TABLE IF EXISTS '${stores.TABLE_NAME}'")
        onCreate(db)
    }

    override fun onCreate(db: SQLiteDatabase) {

        //zeroPayDB에 users 와 stores 테이블 생성
        db.execSQL(SQL_CREATE_TABLE_USERS)
        Log.i("create db","create utable")

        db.execSQL(SQL_CREATE_TABLE_STORES)
        Log.i("create db","create stable")

        db.execSQL(SQL_CREATE_TABLE_DEALS)
        Log.i("create db","create dtable")

    }


    companion object {
        val DATABASE_VERSION=1
        val DATABASE_NAME="zeroPayDB"

        val SQL_CREATE_TABLE_USERS="CREATE TABLE ${users.TABLE_NAME}"+
                "(${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+ users.KEY_NAME+" TEXT,"+
                users.KEY_BALANCE+" INTEGER,"+users.KEY_INCOME+" INTEGER );"
        val SQL_CREATE_TABLE_STORES="CREATE TABLE ${stores.TABLE_NAME}"+
                "(${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+ stores.KEY_NAME+" TEXT,"+
                stores.KEY_ADDR+" TEXT,"+stores.KEY_INFO+" TEXT);"
        val SQL_CREATE_TABLE_DEALS="CREATE TABLE ${deals.TABLE_NAME}"+
                "(${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+
                deals.KEY_DATE+" TEXT,"+ deals.KEY_NAME+" TEXT,"+deals.KEY_PRICE+" INTEGER);"


    }

    class users:BaseColumns{
        companion object {
            val TABLE_NAME="users"
            val KEY_ID="u_id"
            val KEY_NAME="u_name"
            val KEY_BALANCE="u_balance"
            val KEY_INCOME="u_income"
        }
    }
    class stores:BaseColumns{
        companion object {
            val TABLE_NAME="stores"
            val KEY_ID="s_id"
            val KEY_NAME="s_name"
            val KEY_ADDR="s_addr"
            val KEY_INFO="s_info"
        }
    }

    class deals:BaseColumns{
        companion object {
            val TABLE_NAME="deals"
            val KEY_ID="d_id"
            val KEY_DATE="date"
            val KEY_NAME="store"
            val KEY_PRICE="price"
        }
    }




    //db 에 삽입
    fun insertUser(user: user){

        val values=ContentValues().apply {

            put(users.KEY_NAME,user.uname)
            put(users.KEY_BALANCE,user.balance)
            put(users.KEY_INCOME,user.income)
        }

        val success=wdb.insert(users.TABLE_NAME,null,values)
        Log.i("InsertedUserID: ","$success")
    }
    fun insertStore(store: store){

        val stoAddr=store.addr
        if(stoAddr.length>0){
            if(!findSameStore(stoAddr)){
                val values=ContentValues().apply {
                    put(stores.KEY_NAME,store.sname)
                    put(stores.KEY_ADDR,store.addr)
                    put(stores.KEY_INFO,store.info)
                }
                val success=wdb.insert(stores.TABLE_NAME,null,values)
                Log.i("InsertedStoreID: ","$success + storekeyID+ ${store.sid}")

            }
            else {
                Log.i("insert_db","이미 등록한 상점")
                return
            }
        }

    }
    fun insertDeal(deal: deal){

        val values=ContentValues().apply {
            put(deals.KEY_DATE,deal.date)
            put(deals.KEY_NAME,deal.store)
            put(deals.KEY_PRICE,deal.price)
        }

        val success=wdb.insert(deals.TABLE_NAME,null,values)
        Log.i("InsertedDealID: ","$success InsertedDealID:  ${deal.did}")

    }

    //db 에서 삭제
    fun deleteUser(uid:String){
        val deleteUserQuery="DELETE FROM ${users.TABLE_NAME} WHERE ${users.KEY_ID} = ?"
        val selectionArgs=arrayOf(uid)
        wdb.rawQuery(deleteUserQuery,selectionArgs)
    }
    fun deleteStore(sid:String){
        val selection=stores.KEY_ID+"=?"
        val selectionArgs=arrayOf(sid)
        wdb.delete(stores.TABLE_NAME,selection,selectionArgs)
    }
    fun deleteDeal(did: String){
        val selection=deals.KEY_ID+"=?"
        val selectionArgs=arrayOf(did)
        wdb.delete(deals.TABLE_NAME,selection,selectionArgs)
    }

    //db 변경
    fun updateUserIncome(uid:String,income:String){

        val value=ContentValues()
        value.put(users.KEY_INCOME,income)
        val selection="${users.KEY_ID} LIKE ?"
        val selectionArgs=arrayOf(uid)
        val count=wdb.update(users.TABLE_NAME,value,selection,selectionArgs)

        Log.i("updateDB_user","$count")
    }
    fun updateUserBalance(uid:String,balance:String){
        //balance 변경을 많이 쓸 것같아 따로.
        val value=ContentValues()
        value.put(users.KEY_BALANCE,balance)
        val selection="${users.KEY_ID} LIKE ?"
        val selectionArgs=arrayOf(uid)
        val count=wdb.update(users.TABLE_NAME,value,selection,selectionArgs)

        Log.i("updateDB_user","$count")
    }

    fun updateStore(store: store){

        val value=ContentValues().apply {
            put(stores.KEY_NAME,store.sname)
            put(stores.KEY_ADDR,store.addr)
            put(stores.KEY_INFO,store.info)
        }

        val selection="${stores.KEY_ID} LIKE ?"
        val selectionArgs=arrayOf(store.sid)
        val count=wdb.update(stores.TABLE_NAME,value,selection,selectionArgs)

        Log.i("update_db: ","$count")

    }
    fun updateDeal(deal: deal){

        val value=ContentValues().apply {
            put(deals.KEY_DATE,deal.date)
            put(deals.KEY_NAME,deal.store)
            put(deals.KEY_PRICE,deal.price)
        }

        val selection="${deals.KEY_ID} LIKE ?"
        val selectionArgs=arrayOf(deal.did)
        val count=wdb.update(deals.TABLE_NAME,value,selection,selectionArgs)

        Log.i("update_db: ","$count")
    }

    //db  조회
    fun getUser(findID:String):user{

            val selectAllQuery="SELECT * FROM ${users.TABLE_NAME} WHERE ${users.KEY_ID}=?"
            val cursor=rdb.rawQuery(selectAllQuery,arrayOf(findID))

            if(cursor!=null){
                Log.i("finduser",cursor.columnCount.toString())

                with(cursor){
                    while(moveToNext()){

                        var id=cursor.getString(cursor.getColumnIndex(com.example.zeropaytest.DBHelper.users.KEY_ID))

                        if(findID==id){
                            val name=cursor.getString(cursor.getColumnIndex(users.KEY_NAME))
                            val income=cursor.getString(cursor.getColumnIndex(users.KEY_INCOME))
                            val balance=cursor.getString(cursor.getColumnIndex(users.KEY_BALANCE))
                            val result=user(id,name,balance.toInt(),income)
                            return result
                        }

                    }
                    Log.i("findUser","no user")
                }
            }
            val fail=user("","",0,"")
            return fail
        }






    fun getUsers():ArrayList<user>{

        val userList=ArrayList<user>()
        val projection=arrayOf(users.KEY_ID,users.KEY_NAME,users.KEY_BALANCE,users.KEY_INCOME)
        val cursor=rdb.query(users.TABLE_NAME,projection,null,null,null,null,null)
        while(cursor.moveToNext()){
            val uid=cursor.getString(cursor.getColumnIndex(users.KEY_ID))
            val name=cursor.getString(cursor.getColumnIndex(users.KEY_NAME))
            val income=cursor.getString(cursor.getColumnIndex(users.KEY_INCOME))
            val balance=cursor.getString(cursor.getColumnIndex(users.KEY_BALANCE))
            val user=user(uid,name,balance.toInt(),income)
            userList.add(user)
        }
        return userList
    }
    fun getStore(sid:String):store{
           val store=getStores().get(sid.toInt()-1)
        return store
    }
    fun getStores():ArrayList<store>{

        val storeList=ArrayList<store>()
        val projection=arrayOf(stores.KEY_ID,stores.KEY_NAME,stores.KEY_ADDR,stores.KEY_INFO)
        val cursor=rdb.query(stores.TABLE_NAME,projection,null,null,null,null,null,null)
        while(cursor.moveToNext()){
            val sid=cursor.getString(cursor.getColumnIndex(stores.KEY_ID))
            val name=cursor.getString(cursor.getColumnIndex(stores.KEY_NAME))
            val addr=cursor.getString(cursor.getColumnIndex(stores.KEY_ADDR))
            val info=cursor.getString(cursor.getColumnIndex(stores.KEY_INFO))
            val store=store(sid,name,addr,info)
            storeList.add(store)
        }
        return storeList
    }
    fun findSameStore(newaddr:String):Boolean{

        val db=this.readableDatabase
        val selectAllQuery="SELECT * FROM ${stores.TABLE_NAME} WHERE ${stores.KEY_ADDR} =?"
        val selectionArgs=arrayOf(newaddr)
        val cursor=db.rawQuery(selectAllQuery,selectionArgs)

        if(cursor!=null)//같은 것 존재
             return true
        else
             return false
    }
    fun getDeal(did:String):deal{

        var deal=getDeals().get(did.toInt()-1)
        return deal
    }
    fun getDeals():ArrayList<deal>{

        val dealList=ArrayList<deal>()
        val projection=arrayOf(deals.KEY_ID,deals.KEY_NAME,deals.KEY_DATE,deals.KEY_PRICE)
        val cursor=rdb.query(stores.TABLE_NAME,projection,null,null,null,null,null,null)
        while(cursor.moveToNext()){
            val did=cursor.getString(cursor.getColumnIndex(deals.KEY_ID))
            val sname=cursor.getString(cursor.getColumnIndex(deals.KEY_NAME))
            val date=cursor.getString(cursor.getColumnIndex(deals.KEY_DATE))
            val price=cursor.getString(cursor.getColumnIndex(deals.KEY_PRICE))

            val deal=deal(did,sname,date,price)
            dealList.add(deal)
        }
        return dealList
    }


}