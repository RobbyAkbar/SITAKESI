package com.zeecodeku.sitakesi.parsing

import com.zeecodeku.sitakesi.models.AnggotaModel
import org.json.JSONArray
import org.json.JSONException

object AnggotaJSONParser {

    private lateinit var anggotaList: MutableList<AnggotaModel>

    fun parseData(content: String): List<AnggotaModel> {

        lateinit var anggotaArray: JSONArray
        lateinit var anggota: AnggotaModel

        try {
            anggotaArray = JSONArray(content)
            anggotaList = ArrayList()

            for (i in 0 until anggotaArray.length()) {
                val `object` = anggotaArray.getJSONObject(i)
                anggota = AnggotaModel()
                anggota.setIdAnggota(`object`.getString("idAnggota"))
                anggota.setNamaAnggota(`object`.getString("namaAnggota"))
                anggota.setStatusAnggota(`object`.getString("statusAnggota"))
                anggotaList.add(anggota)
            }
            return anggotaList
        } catch (e: JSONException) {
            e.printStackTrace()
            return emptyList()
        }

    }

}
