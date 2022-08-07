package com.zeecodeku.sitakesi.parsing

import com.zeecodeku.sitakesi.models.OrganisasiModel
import org.json.JSONArray
import org.json.JSONException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object OrganisasiJSONParser {

    private lateinit var organisasiList: MutableList<OrganisasiModel>

    fun parseData(content: String): List<OrganisasiModel> {

        lateinit var organisasiArray: JSONArray
        lateinit var organisasi: OrganisasiModel

        try {
            organisasiArray = JSONArray(content)
            organisasiList = ArrayList()

            for (i in 0 until organisasiArray.length()) {
                val `object` = organisasiArray.getJSONObject(i)
                organisasi = OrganisasiModel()
                organisasi.setIdOrganisasi(`object`.getString("idOrganisasi"))
                organisasi.setStatusAnggota(`object`.getString("statusAnggota"))
                organisasi.setNamaOrganisasi(`object`.getString("namaOrganisasi"))
                organisasi.setNamaInstansi(`object`.getString("namaInstansi"))
                organisasi.setStatusOrganisasi(`object`.getString("statusOrganisasi"))
                organisasi.setReferral(`object`.getString("referral"))
                val data = `object`.getString("dtBuat").split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                organisasi.setDtBuat(parseDate(data[0]))
                organisasi.setDtAktif(parseDate(`object`.getString("dtAktif")))
                organisasiList.add(organisasi)
            }
            return organisasiList
        } catch (e: JSONException) {
            e.printStackTrace()
            return emptyList()
        }

    }

    private fun parseDate(tgl: String): String {
        val inputDate = "yyyy-MM-dd"
        val outputDate = "EEEE, MMM dd, yyyy"
        val inputFormat = SimpleDateFormat(inputDate, Locale.US)
        val outputFormat = SimpleDateFormat(outputDate, Locale.US)
        val date: Date
        var string = "null"

        try {
            date = inputFormat.parse(tgl) as Date
            string = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return string
    }

}