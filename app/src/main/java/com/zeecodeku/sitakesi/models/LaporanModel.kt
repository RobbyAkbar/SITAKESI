package com.zeecodeku.sitakesi.models

class LaporanModel {
    private lateinit var idLaporan: String
    private lateinit var ketLaporan: String
    private lateinit var jenisLaporan: String
    private lateinit var jumlah: String
    private lateinit var dtBuat: String

    /*fun getIdLaporan():String {
        return idLaporan
    }*/

    fun setIdLaporan(idLaporan: String) {
        this.idLaporan = idLaporan
    }

    fun getKetLaporan():String {
        return ketLaporan
    }

    fun setKetLaporan(ketLaporan: String) {
        this.ketLaporan = ketLaporan
    }

    fun getJenisLaporan():String {
        return jenisLaporan
    }

    fun setJenisLaporan(jenisLaporan: String) {
        this.jenisLaporan = jenisLaporan
    }

    fun getJumlah():String {
        return jumlah
    }

    fun setJumlah(jumlah: String) {
        this.jumlah = jumlah
    }

    fun getDtBuat(): String {
        return dtBuat
    }

    fun setDtBuat(dtBuat: String) {
        this.dtBuat = dtBuat
    }
}