package com.example.felipeaboytes.appinventario

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.security.AccessControlContext
import java.util.ArrayList

class UserDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{
    companion object {
        //Si cambia la base de datos debera incrementar la version
        val DATABASE_VERSION=1
        val DATABASE_NAME="Usuarios.db"

        //Otros tipos de datos text,float,integer

        private val SQL_CREATE_ENTRIES ="CREATE TABLE Usuario("+"idUsuario text PRIMARY KEY," + "nomUsuario text," +"edadUsuario text)"

        private val SQL_DELETE_ENTRIES ="DROP TABLE IF EXISTS Usuario"
    }

    override fun onCreate(db: SQLiteDatabase)
    {
        db.execSQL(SQL_CREATE_ENTRIES)
        //onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase,oldVersion: Int, newVersion:Int)
    {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
        //onUpgrade(db, oldVersion,newVersion)
    }

    override fun onDowngrade(db:SQLiteDatabase,oldVersion: Int,newVersion: Int)
    {
        onUpgrade(db,oldVersion,newVersion)

    }

    //Rutina Generica para mandar ejecutar un Insert, Update o Delete a la base de datos
    fun Ejecuta(sentencia: String): Int
    {
        try {
            val db= writableDatabase
            db.execSQL(sentencia)
            db.close()
            return 1;

        }catch (ex: Exception ){
            return 0;
        }
    }

    //RUTINA GENERICA PARA MANDAR EJECUTAR UNA CONSULTA A LA BASE DE DATO
    fun Consulta (select: String): Cursor?
    {
        try {
            val db= readableDatabase
            var cur: Cursor=db.rawQuery(select, null)
            return  cur
        } catch (ex: Exception){

            val cur: Cursor? = null
            return cur

        }
    }
}// FIN DE LA CLASE