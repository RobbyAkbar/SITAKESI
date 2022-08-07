package com.zeecodeku.sitakesi.models

import android.os.Parcel
import android.os.Parcelable

class OrganisasiModel() : Parcelable {
    private var idOrganisasi: String? = null
    private var statusAnggota: String? = null
    private var namaOrganisasi: String? = null
    private var namaInstansi: String? = null
    private var statusOrganisasi: String? = null
    private var referral: String? = null
    private var dtBuat: String? = null
    private var dtAktif: String? = null

    constructor(parcel: Parcel) : this() {
        idOrganisasi = parcel.readString()
        statusAnggota = parcel.readString()
        namaOrganisasi = parcel.readString()
        namaInstansi = parcel.readString()
        statusOrganisasi = parcel.readString()
        referral = parcel.readString()
        dtBuat = parcel.readString()
        dtAktif = parcel.readString()
    }

    fun getIdOrganisasi(): String? {
        return idOrganisasi
    }

    fun setIdOrganisasi(idOrganisasi: String) {
        this.idOrganisasi = idOrganisasi
    }

    fun getStatusAnggota(): String? {
        return statusAnggota
    }

    fun setStatusAnggota(statusAnggota: String) {
        this.statusAnggota = statusAnggota
    }

    fun getNamaOrganisasi(): String? {
        return namaOrganisasi
    }

    fun setNamaOrganisasi(namaOrganisasi: String) {
        this.namaOrganisasi = namaOrganisasi
    }

    fun getNamaInstansi(): String? {
        return namaInstansi
    }

    fun setNamaInstansi(namaInstansi: String) {
        this.namaInstansi = namaInstansi
    }

    fun getStatusOrganisasi(): String? {
        return statusOrganisasi
    }

    fun setStatusOrganisasi(statusOrganisasi: String) {
        this.statusOrganisasi = statusOrganisasi
    }

    fun getReferral(): String? {
        return referral
    }

    fun setReferral(referral: String) {
        this.referral = referral
    }

    fun getDtBuat(): String? {
        return dtBuat
    }

    fun setDtBuat(dtBuat: String) {
        this.dtBuat = dtBuat
    }

    fun getDtAktif(): String? {
        return dtAktif
    }

    fun setDtAktif(dtAktif: String) {
        this.dtAktif = dtAktif
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idOrganisasi)
        parcel.writeString(statusAnggota)
        parcel.writeString(namaOrganisasi)
        parcel.writeString(namaInstansi)
        parcel.writeString(statusOrganisasi)
        parcel.writeString(referral)
        parcel.writeString(dtBuat)
        parcel.writeString(dtAktif)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrganisasiModel> {
        override fun createFromParcel(parcel: Parcel): OrganisasiModel {
            return OrganisasiModel(parcel)
        }

        override fun newArray(size: Int): Array<OrganisasiModel?> {
            return arrayOfNulls(size)
        }
    }

}
