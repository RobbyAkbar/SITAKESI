package com.zeecodeku.sitakesi.parsing

import com.zeecodeku.sitakesi.models.TagihanModel
import org.json.JSONArray
import org.json.JSONException

object TagihanJSONParser {

    private lateinit var tagihanList: MutableList<TagihanModel>

    fun parseData(content: String): List<TagihanModel> {

        lateinit var tagihanArray: JSONArray
        lateinit var tagihan: TagihanModel

        try {
            tagihanArray = JSONArray(content)
            tagihanList = ArrayList()

            for (i in 0 until tagihanArray.length()) {
                val `object` = tagihanArray.getJSONObject(i)
                tagihan = TagihanModel()
                tagihan.setIdTagihan(`object`.getString("idTagihan"))
                tagihan.setNamaTagihan(`object`.getString("namaTagihan"))
                tagihan.setJumlahTagihan(`object`.getString("jumlahTagihan"))
                tagihan.setDtDeadline(`object`.getString("dtDeadline"))
                tagihan.setStatusTagihan(`object`.getString("statusTagihan"))
                tagihanList.add(tagihan)
            }
            return tagihanList
        } catch (e: JSONException) {
            e.printStackTrace()
            return emptyList()
        }

    }

}