package com.rino.self_services.utils

object Constants {

 //   val base_url = "https://rino-app-staging.azurewebsites.net/"

 //   val base_url = "https://rino-app.azurewebsites.net/"

   val base_url = "https://278b-41-37-174-121.ngrok.io"
  
    val grant_type = "password"
    val client_id = "Rhino.Android"

    fun convertNumsToArabic(value: String): String {
        return (value.replace("1", "١").replace("2", "٢")
            .replace("3", "٣").replace("4", "٤")
            .replace("5", "٥").replace("6", "٦")
            .replace("7", "٧").replace("8", "٨")
            .replace("9", "٩").replace("0", "٠").replace("-","/")
            .replace("AM", "ص").replace("PM", "م"))
    }
}
fun String.dateToArabic(): String {
    return (this.replace("1", "١").replace("2", "٢")
        .replace("3", "٣").replace("4", "٤")
        .replace("5", "٥").replace("6", "٦")
        .replace("7", "٧").replace("8", "٨")
        .replace("9", "٩").replace("0", "٠").replace("-","/")
        .replace("AM", "ص").replace("PM", "م"))
}
fun String.numToArabic(): String {
    return (this.replace("1", "١").replace("2", "٢")
        .replace("3", "٣").replace("4", "٤")
        .replace("5", "٥").replace("6", "٦")
        .replace("7", "٧").replace("8", "٨")
        .replace("9", "٩").replace("0", "٠"))
}
fun String.numToEnglish(): String {
    return (this.replace("١", "1").replace("٢", "2")
        .replace("٣", "3").replace("٤", "4")
        .replace("٥", "5").replace("٦", "6")
        .replace("٧", "7").replace("٨", "8")
        .replace("٩", "9").replace("٠", "0")
            )
}