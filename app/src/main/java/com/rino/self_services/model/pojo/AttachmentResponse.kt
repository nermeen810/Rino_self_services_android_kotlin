package com.rino.self_services.model.pojo
data class AttachmentResponse(var data:AttachmentResponseData)
data class AttachmentResponseData (var id:Int,var status_code:String)
/*
{
    "data": {
        "id": 3930,
        "action": {
            "name": "اعتماد مدير الموارد البشرية",
            "since": "2022-06-02T00:06:04",
            "cancel": false,
            "valid": true,
            "users": [
                "AC_BRITON2"
            ]
        },
        "status_code": "InsuranceOfficer",
        "status": "تم اعتماد إدارة التأمين",
        "status_date": "2022-06-02T00:06:04",
        "status_by": "وليد محمد محمد احمد",
        "current": {
            "name": "اعتماد مدير الموارد البشرية",
            "users": [
                "وليد محمد محمد احمد",
                "عبد العالي رداد رده الفوني الربيعي",
                "سعد محمد عبدالله البسامي"
            ]
        },
        "step": 7
    },
    "success": true,
    "message": null
}
 */