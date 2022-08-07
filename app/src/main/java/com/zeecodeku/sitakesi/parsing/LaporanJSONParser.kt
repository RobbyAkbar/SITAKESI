package com.zeecodeku.sitakesi.parsing

import com.zeecodeku.sitakesi.models.LaporanModel
import org.json.JSONArray
import org.json.JSONException

object LaporanJSONParser {

    private lateinit var laporanList: MutableList<LaporanModel>

    fun parseData(content: String): List<LaporanModel> {

        lateinit var laporanArray: JSONArray
        lateinit var laporan: LaporanModel

        try {
            laporanArray = JSONArray(content)
            laporanList = ArrayList()

            for (i in 0 until laporanArray.length()) {
                val `object` = laporanArray.getJSONObject(i)
                laporan = LaporanModel()
                laporan.setIdLaporan(`object`.getString("idLaporan"))
                laporan.setKetLaporan(`object`.getString("ketLaporan"))
                laporan.setJenisLaporan(`object`.getString("jenisLaporan"))
                laporan.setJumlah(`object`.getString("jumlah"))
                val data = `object`.getString("dtBuat").split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                laporan.setDtBuat((data[0]))
                laporanList.add(laporan)
            }
            return laporanList
        } catch (e: JSONException) {
            e.printStackTrace()
            return emptyList()
        }

    }

}