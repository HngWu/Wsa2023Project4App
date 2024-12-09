package com.example.wsa2023project4app.Api

import com.example.wsa2023project4app.Models.Municipality
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GetMunicipalities {
    open fun getFunction(name: String): Municipality? {
        val url = URL("http://10.0.2.2:5197/Navigation/getmunicipalitymap/$name")

        try {
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.setRequestProperty("Content-Type", "application/json; utf-8")
            con.setRequestProperty("Accept", "application/json")
            //con.connectTimeout = 1000

            val status = con.responseCode
            if (status == 200) {
                val reader = BufferedReader(InputStreamReader(con.inputStream))
                val jsonData = reader.use { it.readText() }
                reader.close()

                val objectList = Json.decodeFromString<Municipality>(jsonData) as Municipality?


                return objectList


            }
            con.disconnect()
        } catch (e: Exception) {
            return null
        }
        return null
    }
}