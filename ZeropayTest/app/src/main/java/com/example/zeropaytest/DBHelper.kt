package com.example.todayzero.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.example.zeropaytest.deal
import com.example.zeropaytest.store
import com.example.zeropaytest.user

class DBHelper(context: Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION){


    val zone= mutableMapOf<Int,String>(1 to "강남구",2 to "강동구", 3 to "강북구", 4 to "강서구", 5 to "관악구", 6 to "광진구", 7 to "구로구", 8 to "금천구", 9 to "노원구", 10 to "도봉구", 11 to "동대문구", 12 to "동작구", 13 to "마포구", 14 to "서대문구", 15  to "서초구", 16 to "성동구", 17 to "성북구", 18 to "송파구", 19 to "양천구", 20 to "영등포구", 21 to "용산구", 22 to "은평구", 23 to "종로구", 24 to "중구", 25 to "중랑구")
    val wdb=writableDatabase
    val rdb=readableDatabase

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        //DB 수정이 필요한 경우
        db.execSQL("DROP TABLE IF EXISTS '${users.TABLE_NAME}'")
        for(gu in 1..25){
            var sql= "DROP TABLE IF EXISTS '${zone[gu]}'"
            db.execSQL(sql)
            Log.i("create db","create stable ${zone[gu]}")
        }
        db.execSQL("DROP TABLE IF EXISTS '${deals.TABLE_NAME}'")
        onCreate(db)
    }

    override fun onCreate(db: SQLiteDatabase) {

        //zeroPayDB에 users 와 stores 테이블 생성
        db.execSQL(SQL_CREATE_TABLE_USERS)
        Log.i("create db","create utable")

        for(gu in 1..25){
            var sql= SQL_CREATE_TABLE_STORES_PRE+"${zone[gu]}"+ SQL_CREATE_TABLE_STORES_POST
            db.execSQL(sql)
            Log.i("create db","create stable ${zone[gu]}")
        }


        db.execSQL(SQL_CREATE_TABLE_DEALS)
        Log.i("create db","create dtable")

    }

    fun createTable(){

        //zeroPayDB에 users 와 stores 테이블 생성
        wdb.execSQL(SQL_CREATE_TABLE_USERS)
        Log.i("create db","create utable")

        for(gu in 1..25){
            var sql= SQL_CREATE_TABLE_STORES_PRE+"${zone[gu]}"+ SQL_CREATE_TABLE_STORES_POST
            wdb.execSQL(sql)
            Log.i("create db","create stable ${zone[gu]}")
        }


        wdb.execSQL(SQL_CREATE_TABLE_DEALS)
        Log.i("create db","create dtable")
    }

    companion object {
        val DATABASE_VERSION=1
        val DATABASE_NAME="zeroPayDB"

        val SQL_CREATE_TABLE_USERS="CREATE TABLE ${users.TABLE_NAME}"+
                "(${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+ users.KEY_NAME+" TEXT,"+
                users.KEY_BALANCE+" INTEGER,"+users.KEY_INCOME+" TEXT );"

        /*
        val SQL_CREATE_TABLE_STORES="CREATE TABLE ${stores.TABLE_NAME}"+
                "(${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+ stores.KEY_NAME+" TEXT,"+
                stores.KEY_ADDR+" TEXT,"+stores.KEY_INFO+" TEXT);"
         */


        val SQL_CREATE_TABLE_STORES_PRE="CREATE TABLE ${stores.TABLE_NAME}"
        val SQL_CREATE_TABLE_STORES_POST="(${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+ stores.KEY_NAME+" TEXT,"+
                stores.KEY_ADDR+" TEXT,"+stores.KEY_GU+" TEXT,"+stores.KEY_INFO+" TEXT);"


        val SQL_CREATE_TABLE_DEALS="CREATE TABLE ${deals.TABLE_NAME}"+
                "(${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+
                deals.KEY_DATE+" TEXT,"+ deals.KEY_NAME+" TEXT,"+deals.KEY_PRICE+" TEXT,"+deals.KEY_CATEGORY+" TEXT,"+deals.KEY_ISZERO+" INTEGER);"


    }

    class users:BaseColumns{
        companion object {
            val TABLE_NAME="users"
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
            val KEY_GU="s_gu"
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
            val KEY_CATEGORY="category"
            val KEY_ISZERO="is_zeropay"
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
           // if(findSameStore(stoAddr,store.locality)){
                if(findSameStore(stoAddr,store.locality)){
                val values=ContentValues().apply {
                    put(stores.KEY_NAME,store.sname)
                    put(stores.KEY_ADDR,store.addr)
                    put(stores.KEY_GU,store.locality)
                    put(stores.KEY_INFO,store.info)
                }
                val success=wdb.insert(stores.TABLE_NAME+store.locality,null,values)
                Log.i("InsertedStoreID: ","${store.locality} +$success + storekeyID+ ${store.sid}")

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
            put(deals.KEY_CATEGORY,deal.category)
            put(deals.KEY_ISZERO,deal.isZero)
        }

        val success=wdb.insert(deals.TABLE_NAME,null,values)
        Log.i("InsertedDealID: ","$success InsertedDealID:  ${BaseColumns._ID}")

    }

    //db 에서 삭제
    fun deleteUser(){
        wdb.delete(users.TABLE_NAME,null,null)
    }
    fun deleteStore(sid:String,gu:String){   //gu 에 삭제할 store.locatlity  입력

        val selection="${BaseColumns._ID} = ?"
        val selectionArgs=arrayOf(sid)
        wdb.delete(stores.TABLE_NAME+gu,selection,selectionArgs)
    }
    fun deleteDeal(did: String){
        val selection="${BaseColumns._ID} = ?"
        val selectionArgs=arrayOf(did)
        wdb.delete(deals.TABLE_NAME,selection,selectionArgs)
    }

    //db 변경
    fun updateUserIncome(income:String){

        val value=ContentValues()
        value.put(users.KEY_INCOME,income)
        //val selection="${users.KEY_ID} LIKE ?"
        //val selectionArgs=arrayOf(uid)
        //val count=wdb.update(users.TABLE_NAME,value,selection,selectionArgs)
        val count=wdb.update(users.TABLE_NAME,value,null,null)

        Log.i("updateDB_user","$count")
    }
    fun updateUserBalance(balance:String){
        //balance 변경을 많이 쓸 것같아 따로.
        val value=ContentValues()
        value.put(users.KEY_BALANCE,balance)
        //val selection="${users.KEY_ID} LIKE ?"
        //val selectionArgs=arrayOf(uid)
        val count=wdb.update(users.TABLE_NAME,value,null,null)

        Log.i("updateDB_user","$count")
    }

    fun updateStore(store: store){

        val value=ContentValues().apply {
            put(stores.KEY_NAME,store.sname)
            put(stores.KEY_ADDR,store.addr)
            put(stores.KEY_GU,store.locality)
            put(stores.KEY_INFO,store.info)
        }

        val selection="${BaseColumns._ID} LIKE ?"
        val selectionArgs=arrayOf(store.sid)
        val count=wdb.update(stores.TABLE_NAME+store.locality,value,selection,selectionArgs)

        Log.i("update_db: ","$count")

    }
    fun updateDeal(deal: deal){

        val value=ContentValues().apply {
            put(deals.KEY_DATE,deal.date)
            put(deals.KEY_NAME,deal.store)
            put(deals.KEY_PRICE,deal.price)
            put(deals.KEY_CATEGORY,deal.category)
            put(deals.KEY_ISZERO,deal.isZero)
        }

        val selection="${BaseColumns._ID} LIKE ?"
        val selectionArgs=arrayOf(deal.did)
        val count=wdb.update(deals.TABLE_NAME,value,selection,selectionArgs)

        Log.i("update_db: ","$count")
    }

    //db  조회
    fun getUser(): user {

        val selectAllQuery="SELECT * FROM ${users.TABLE_NAME}"
        val cursor=rdb.rawQuery(selectAllQuery,null)

        if(cursor!=null){
            Log.i("finduser",cursor.columnCount.toString())

            with(cursor){
                while(moveToNext()){
                    val name=cursor.getString(cursor.getColumnIndex(users.KEY_NAME))
                    val income=cursor.getString(cursor.getColumnIndex(users.KEY_INCOME))
                    val balance=cursor.getInt(cursor.getColumnIndex(users.KEY_BALANCE))
                    val result=user(name,balance,income)
                    return result
                }

                Log.i("findUser","no user")
            }
        }
        val fail=user("",0,"")
        return fail
    }

    fun getDealsSize() { //gu: 찾으려는 store의 locality
        val selectAllQuery = "SELECT * FROM ${deals.TABLE_NAME}"
        val cursor = rdb.rawQuery(selectAllQuery, null)
        val size=cursor.columnNames

        for(i in size){
            Log.i("dealsize", i.toString())

        }
        Log.i("dealszie1",size.size.toString())


    }


    fun getStore() { //gu: 찾으려는 store의 locality
        val str = stores.TABLE_NAME + "마포구"
        val selectAllQuery = "SELECT * FROM $str"
        val cursor = rdb.rawQuery(selectAllQuery, null)

        val size=cursor.columnNames
        for(i in size){
            Log.i("storesize", i.toString())

        }
    }
    fun getStores(gu:String):ArrayList<store>{
            Log.i("getStore","getstore")
        val storeList=ArrayList<store>()
        val projection=arrayOf(BaseColumns._ID, DBHelper.stores.KEY_NAME, DBHelper.stores.KEY_ADDR,DBHelper.stores.KEY_GU,
            DBHelper.stores.KEY_INFO)
        val cursor=rdb.query(DBHelper.stores.TABLE_NAME+gu,projection,null,null,null,null,null,null)
        if(cursor!=null) {
            Log.i("getStore","cursor")
            while (cursor.moveToNext()) {
                val sid = cursor.getString(cursor.getColumnIndex(BaseColumns._ID))
                val name = cursor.getString(cursor.getColumnIndex(DBHelper.stores.KEY_NAME))
                val addr = cursor.getString(cursor.getColumnIndex(DBHelper.stores.KEY_ADDR))
                val gu = cursor.getString(cursor.getColumnIndex(DBHelper.stores.KEY_GU))
                val info = cursor.getString(cursor.getColumnIndex(DBHelper.stores.KEY_INFO))
                val store = store(sid, name, addr, gu, info)
                Log.i("getStore",store.sid)

                storeList.add(store)
            }
        }
        else{
            Log.i("searchStores","no store")
        }
        return storeList
    }
    fun findSameStore(newaddr:String,gu: String):Boolean{

        val db=this.readableDatabase
        val selectAllQuery="SELECT * FROM ${stores.TABLE_NAME}$gu WHERE ${stores.KEY_ADDR} LIKE ?"
        Log.i("findSameStore",stores.TABLE_NAME+gu)
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
        val projection=arrayOf(BaseColumns._ID,deals.KEY_NAME,deals.KEY_DATE,deals.KEY_PRICE,deals.KEY_CATEGORY,deals.KEY_ISZERO)
        val cursor=rdb.query(deals.TABLE_NAME,projection,null,null,null,null,null,null)
        if(cursor!=null) {
            while (cursor.moveToNext()) {
                val did = cursor.getString(cursor.getColumnIndex(BaseColumns._ID))
                val sname = cursor.getString(cursor.getColumnIndex(deals.KEY_NAME))
                val date = cursor.getString(cursor.getColumnIndex(deals.KEY_DATE))
                val price = cursor.getString(cursor.getColumnIndex(deals.KEY_PRICE))
                val category = cursor.getString(cursor.getColumnIndex(deals.KEY_CATEGORY))
                val isZero = cursor.getInt(cursor.getColumnIndex(deals.KEY_ISZERO))

                val deal = deal(did, sname, date, price, category, isZero)
                dealList.add(deal)
            }
        }else{
            Log.i("searchdeal","nodeal")
        }
        return dealList
    }


}