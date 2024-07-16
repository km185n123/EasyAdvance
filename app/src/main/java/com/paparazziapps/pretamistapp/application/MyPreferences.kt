package com.paparazziteam.yakulap.helper.applicacion

import android.graphics.Color
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MyPreferences {

    val prefs = CacheData()

    inline fun <reified T : Any> saveObject(key: String, value: T) {
        val jsonString = Json.encodeToString(value)
        prefs.setString(key, jsonString)
    }

    inline fun <reified T> getObject(key: String): T? {
        val jsonString = prefs.getString(key, "")
        return jsonString.let {
            Json.decodeFromString(it)
        }
    }





    //PREFERENCES STRING
    var email_login: String
        get()      = prefs.getString("email_login", "")
        set(value) = prefs.setString("email_login", value)

    var sucursalName: String
        get()      = prefs.getString("sucursalName", "")
        set(value) = prefs.setString("sucursalName", value)

    //Sucursales All
    var sucusales: String
    get()      = prefs.getString("sucusales", "")
    set(value) = prefs.setString("sucusales", value)


    //PREFERENCES BOOLEAN
    var isLogin: Boolean
        get()      = prefs.getBoolean("isLogin", false)
        set(value) = prefs.setBoolean("isLogin", value)

    ////Logica de datos de registro de datos
    var isAdmin: Boolean
        get()      = prefs.getBoolean("isAdmin", false)
        set(value) = prefs.setBoolean("isAdmin", value)

    var isSuperAdmin: Boolean
        get()      = prefs.getBoolean("isSuperAdmin", false)
        set(value) = prefs.setBoolean("isSuperAdmin", value)

    var isActiveUser: Boolean
        get()      = prefs.getBoolean("isActiveUser", false)
        set(value) = prefs.setBoolean("isActiveUser", value)


    //PREFERENCES INTEGER
    var color: Int
        get() = prefs.getInt("color",  Color.parseColor("#ff0066"))
        set(value) = prefs.setInt("color", value)

    var sucursalId: Int
        get()      = prefs.getInt("sucursalId", INT_DEFAULT)
        set(value) = prefs.setInt("sucursalId", value)


    fun removeLoginData(){
        prefs.remove("isLogin")
        prefs.remove("email_login")
        prefs.remove("sucursalName")
        prefs.remove("isAdmin")
        prefs.remove("isSuperAdmin")
        prefs.remove("isActiveUser")
        prefs.remove("sucursalId")
    }
}