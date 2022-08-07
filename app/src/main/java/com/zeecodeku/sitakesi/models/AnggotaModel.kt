package com.zeecodeku.sitakesi.models

class AnggotaModel {
    private lateinit var idAnggota: String
    private lateinit var namaAnggota: String
    private lateinit var statusAnggota: String

    fun getIdAnggota():String {
        return idAnggota
    }

    fun setIdAnggota(idAnggota: String) {
        this.idAnggota = idAnggota
    }

    fun getNamaAnggota(): String {
        return namaAnggota
    }

    fun setNamaAnggota(namaAnggota: String) {
        this.namaAnggota = namaAnggota
    }

    fun getStatusAnggota(): String {
        return statusAnggota
    }

    fun setStatusAnggota(statusAnggota: String) {
        this.statusAnggota = statusAnggota
    }
}
