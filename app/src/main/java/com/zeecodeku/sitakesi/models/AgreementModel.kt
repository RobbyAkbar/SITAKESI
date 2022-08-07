package com.zeecodeku.sitakesi.models

class AgreementModel{
    private lateinit var agreeId: String
    private lateinit var agreeName: String

    fun getAgreeId():String {
        return agreeId
    }

    fun setAgreeId(agreeId: String) {
        this.agreeId = agreeId
    }

    fun getAgreeName(): String {
        return agreeName
    }

    fun setAgreeName(agreeName: String) {
        this.agreeName = agreeName
    }
}