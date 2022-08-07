package com.zeecodeku.sitakesi.models

import android.os.Parcel
import android.os.Parcelable

class TagihanModel() : Parcelable {
    private var idTagihan: String? = null
    private var namaTagihan: String? = null
    private var jumlahTagihan: String? = null
    private var dtDeadline: String? = null
    private var statusTagihan: String? = null

    constructor(parcel: Parcel) : this() {
        idTagihan = parcel.readString()
        namaTagihan = parcel.readString()
        jumlahTagihan = parcel.readString()
        dtDeadline = parcel.readString()
        statusTagihan = parcel.readString()
    }

    fun getIdTagihan():String? {
        return idTagihan
    }

    fun setIdTagihan(idTagihan: String) {
        this.idTagihan = idTagihan
    }

    fun getNamaTagihan(): String? {
        return namaTagihan
    }

    fun setNamaTagihan(namaTagihan: String) {
        this.namaTagihan = namaTagihan
    }

    fun getJumlahTagihan(): String? {
        return jumlahTagihan
    }

    fun setJumlahTagihan(jumlahTagihan: String) {
        this.jumlahTagihan = jumlahTagihan
    }

    fun getDtDeadline(): String? {
        return dtDeadline
    }

    fun setDtDeadline(dtDeadline: String) {
        this.dtDeadline = dtDeadline
    }

    fun getStatusTagihan(): String? {
        return statusTagihan
    }

    fun setStatusTagihan(statusTagihan: String) {
        this.statusTagihan = statusTagihan
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idTagihan)
        parcel.writeString(namaTagihan)
        parcel.writeString(jumlahTagihan)
        parcel.writeString(dtDeadline)
        parcel.writeString(statusTagihan)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TagihanModel> {
        override fun createFromParcel(parcel: Parcel): TagihanModel {
            return TagihanModel(parcel)
        }

        override fun newArray(size: Int): Array<TagihanModel?> {
            return arrayOfNulls(size)
        }
    }
}
