package com.example.wsa2023project4app.Api

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.wsa2023project4app.Models.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class Login {
    fun postFunction(
        user: User,

    ): Boolean {
        val url = URL("http://10.0.2.2:5197/Navigation/login")

        try {
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.setRequestProperty("Content-Type", "application/json; utf-8")
            con.setRequestProperty("Accept", "application/json")
            con.doOutput = true

            val json = Json.encodeToString(user)
            val os = OutputStreamWriter(con.outputStream)

            os.write(json)
            os.flush()
            os.close()

            val status = con.responseCode
            if (status == 200) {
                return true
            }
            else{
                return false

            }
        } catch (e: Exception) {
            return false

        }
    }

}