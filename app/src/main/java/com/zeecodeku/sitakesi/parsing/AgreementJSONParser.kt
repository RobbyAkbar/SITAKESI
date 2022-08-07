package com.zeecodeku.sitakesi.parsing

import com.zeecodeku.sitakesi.models.AgreementModel
import org.json.JSONArray
import org.json.JSONException

object AgreementJSONParser {

    private lateinit var agreementList: MutableList<AgreementModel>

    fun parseData(content: String): List<AgreementModel> {

        lateinit var agreeArray: JSONArray
        lateinit var agree: AgreementModel

        try {
            agreeArray = JSONArray(content)
            agreementList = ArrayList()

            for (i in 0 until agreeArray.length()) {
                val `object` = agreeArray.getJSONObject(i)
                agree = AgreementModel()
                agree.setAgreeId(`object`.getString("idAgree"))
                agree.setAgreeName(`object`.getString("nameAgree"))
                agreementList.add(agree)
            }
            return agreementList
        } catch (e: JSONException) {
            e.printStackTrace()
            return emptyList()
        }

    }

}
