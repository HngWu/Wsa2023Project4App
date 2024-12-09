package com.example.wsa2023project4app.Api

import com.example.wsa2023project4app.Models.TouristSpot
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class GetTouristSpots {
    fun getFunction(name: String): MutableList<TouristSpot>? {
        val url = URL("http://10.0.2.2:5197/Navigation/gettouristspot/$name")

        try {
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.setRequestProperty("Content-Type", "application/json; utf-8")
            con.setRequestProperty("Accept", "application/json")



            val status = con.responseCode
            if (status == 200) {

                val reader = BufferedReader(InputStreamReader(con.inputStream))
                val jsonData = reader.use { it.readText() }
                reader.close()

                val objectList = Json.decodeFromString(jsonData) as MutableList<TouristSpot>?

                return objectList

            }
        }catch (e: Exception) {
            return null
        }
        return null
    }



}