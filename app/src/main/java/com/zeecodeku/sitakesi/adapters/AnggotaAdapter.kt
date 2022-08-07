package com.zeecodeku.sitakesi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.zeecodeku.sitakesi.R
import com.zeecodeku.sitakesi.models.AnggotaModel
import com.zeecodeku.sitakesi.utils.Config

class AnggotaAdapter(
    private val args: String, private val context: Context?
) : RecyclerView.Adapter<AnggotaAdapter.AnggotaViewHolder>() {

    private var mDataset: List<AnggotaModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): AnggotaViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_anggota, parent, false)

        return AnggotaViewHolder(v)
    }

    override fun onBindViewHolder(anggotaViewHolder: AnggotaViewHolder, i: Int) {
        anggotaViewHolder.bindItem(mDataset[i])
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    fun updateWith(anggotaList: List<AnggotaModel>) {
        this.mDataset = anggotaList
        notifyDataSetChanged()
    }

    inner class AnggotaViewHolder(v: View) : RecyclerView.ViewHolder(v){
        private var nama: TextView = v.findViewById(R.id.tvNama)
        private var status: TextView = v.findViewById(R.id.tvStatus)
        private var button: Button = v.findViewById(R.id.btnAnggota)

        fun bindItem(anggota: AnggotaModel){
            nama.text = anggota.getNamaAnggota()
            context!!

            when {
                anggota.getStatusAnggota()=="anggota" -> {
                    button.text = context.getString(R.string.select)
                    status.text = context.getString(R.string.member)
                }
                anggota.getStatusAnggota()=="ketua" -> {
                    button.visibility = View.GONE
                    status.text = context.getString(R.string.leader)
                }
                anggota.getStatusAnggota()=="bendahara" -> {
                    button.text = context.getString(R.string.deselect)
                    status.text = context.getString(R.string.treasurer)
                }
            }

            button.setOnClickListener {
                var statusAnggota = ""
                if (anggota.getStatusAnggota()=="bendahara") statusAnggota = "anggota"
                else if (anggota.getStatusAnggota()=="anggota") statusAnggota = "bendahara"
                val queue = Volley.newRequestQueue(context)
                val stringRequest = object : StringRequest(Method.POST, Config.SET_URL,
                    Response.Listener<String> { response ->
                        if (response.contains("success")){
                            if (anggota.getStatusAnggota()=="bendahara") {
                                status.text = context.getString(R.string.member)
                                button.text = context.getString(R.string.select)
                                anggota.setStatusAnggota("anggota")
                            } else if (anggota.getStatusAnggota()=="anggota") {
                                status.text = context.getString(R.string.treasurer)
                                button.text = context.getString(R.string.deselect)
                                anggota.setStatusAnggota("bendahara")
                            }
                            Toast.makeText(context, context.getString(R.string.status_change), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, context.getString(R.string.status_message), Toast.LENGTH_SHORT).show()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(context, context.getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
                    }){
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params[Config.ID_ORGANISASI_PARAM] = args
                        params[Config.ID_ANGGOTA_SHARED_PREF] = anggota.getIdAnggota()
                        params[Config.STATUS_ANGGOTA_SHARED_PREF] = statusAnggota
                        return params
                    }
                }
                queue.add(stringRequest)
                notifyDataSetChanged()
            }
        }
    }
}
