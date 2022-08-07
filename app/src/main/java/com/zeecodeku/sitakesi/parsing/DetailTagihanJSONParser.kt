package com.zeecodeku.sitakesi.parsing

import com.zeecodeku.sitakesi.models.DetailTagihanModel
import org.json.JSONArray
import org.json.JSONException

object DetailTagihanJSONParser {

    private lateinit var detailList: MutableList<DetailTagihanModel>

    fun parseData(content: String): List<DetailTagihanModel> {

        lateinit var detailArray: JSONArray
        lateinit var detail: DetailTagihanModel

        try {
            detailArray = JSONArray(content)
            detailList = ArrayList()

            for (i in 0 until detailArray.length()) {
                val `object` = detailArray.getJSONObject(i)
                detail = DetailTagihanModel()
                detail.setIdAnggota(`object`.getString("idAnggota"))
                detail.setNamaAnggota(`object`.getString("namaAnggota"))
                detail.setStatusAnggota(`object`.getString("statusAnggota"))
                detail.setTotalBayar(`object`.getString("totalBayar"))
                detail.setStatusTagihan(`object`.getString("statusTagihan"))
                detailList.add(detail)
            }
            return detailList
        } catch (e: JSONException) {
            e.printStackTrace()
            return emptyList()
        }

    }

}