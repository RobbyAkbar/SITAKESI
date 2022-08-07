package com.zeecodeku.sitakesi.models

import android.os.Parcel
import android.os.Parcelable

class DetailTagihanModel() : Parcelable {
    private var idAnggota: String? = null
    private var namaAnggota: String? = null
    private var statusAnggota: String? = null
    private var totalBayar: String? = null
    private var statusTagihan: String? = null

    constructor(parcel: Parcel) : this() {
        idAnggota = parcel.readString()
        namaAnggota = parcel.readString()
        statusAnggota = parcel.readString()
        totalBayar = parcel.readString()
        statusTagihan = parcel.readString()
    }

    fun getIdAnggota():String? {
        return idAnggota
    }

    fun setIdAnggota(idAnggota: String) {
        this.idAnggota = idAnggota
    }

    fun getNamaAnggota(): String? {
        return namaAnggota
    }

    fun setNamaAnggota(namaAnggota: String) {
        this.namaAnggota = namaAnggota
    }

    fun getStatusAnggota(): String? {
        return statusAnggota
    }

    fun setStatusAnggota(statusAnggota: String) {
        this.statusAnggota = statusAnggota
    }

    fun getTotalBayar(): String? {
        return totalBayar
    }

    fun setTotalBayar(totalBayar: String) {
        this.totalBayar = totalBayar
    }

    fun getStatusTagihan(): String? {
        return statusTagihan
    }

    fun setStatusTagihan(statusTagihan: String) {
        this.statusTagihan = statusTagihan
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idAnggota)
        parcel.writeString(namaAnggota)
        parcel.writeString(statusAnggota)
        parcel.writeString(totalBayar)
        parcel.writeString(statusTagihan)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DetailTagihanModel> {
        override fun createFromParcel(parcel: Parcel): DetailTagihanModel {
            return DetailTagihanModel(parcel)
        }

        override fun newArray(size: Int): Array<DetailTagihanModel?> {
            return arrayOfNulls(size)
        }
    }
}
