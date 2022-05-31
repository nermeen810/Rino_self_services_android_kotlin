package com.rino.self_services.model.pojo


class SeeAllRequest{
    var token:String = ""
    var me:String = ""
    var currentFutuer = ""
    var from = ""
    var to = ""
    var page = 1

    constructor()
    constructor(token:String,me:String,currentFutuer: String,from: String,to:String,page:Int){
        this.token = token
        this.me = me
        this.currentFutuer = currentFutuer
        this.to = to
        this.from = from
        this.page = page

    }
}
