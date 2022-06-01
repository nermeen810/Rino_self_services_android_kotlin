package com.rino.self_services.model.pojo


class SeeAllRequest{
    var token:String = ""
    var me:String = ""
    var currentFutuer = ""
    var from = ""
    var to = ""
    var page:Long = 0

    constructor()
    constructor(token:String,me:String,currentFutuer: String,from: String,to:String,page:Long){
        this.token = token
        this.me = me
        this.currentFutuer = currentFutuer
        this.to = to
        this.from = from
        this.page = page

    }
}
