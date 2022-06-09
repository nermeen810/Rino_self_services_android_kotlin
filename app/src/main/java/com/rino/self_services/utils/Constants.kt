package com.rino.self_services.utils

object Constants {
    val base_url = "https://rino-app-staging.azurewebsites.net/"

//    val base_url = "https://7a28-156-214-121-181.ngrok.io/"
  
    val grant_type = "password"
    val client_id = "Rhino.Android"

    fun convertNumsToArabic(value: String): String {
        return (value.replace("1", "١").replace("2", "٢")
            .replace("3", "٣").replace("4", "٤")
            .replace("5", "٥").replace("6", "٦")
            .replace("7", "٧").replace("8", "٨")
            .replace("9", "٩").replace("0", "٠")
            .replace("AM", "ص").replace("PM", "م"))
    }
}