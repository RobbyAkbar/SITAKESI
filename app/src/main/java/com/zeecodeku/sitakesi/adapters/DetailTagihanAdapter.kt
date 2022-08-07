package com.zeecodeku.sitakesi.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.zeecodeku.sitakesi.R
import com.zeecodeku.sitakesi.helpers.InputValidation
import com.zeecodeku.sitakesi.listeners.OnRefreshViewListner
import com.zeecodeku.sitakesi.models.DetailTagihanModel
import com.zeecodeku.sitakesi.models.TagihanModel
import com.zeecodeku.sitakesi.utils.AppUtils
import com.zeecodeku.sitakesi.utils.Config
import java.text.DecimalFormat

class DetailTagihanAdapter (
    private val tagihanModel: TagihanModel, private val title: String, private val context: Context?
): RecyclerView.Adapter<DetailTagihanAdapter.DetailTagihanViewHolder>() {

    private var mDataset: List<DetailTagihanModel> = emptyList()
    private var onRefreshViewListner: OnRefreshViewListner = context as OnRefreshViewListner


    override fun onCreateViewHolder(parent: ViewGroup, i: Int): DetailTagihanViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_tagihan, parent, false)

        return DetailTagihanViewHolder(v)
    }

    override fun onBindViewHolder(detailTagihanViewHolder: DetailTagihanViewHolder, i: Int) {
        detailTagihanViewHolder.bindItem(mDataset[i])
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    fun updateWith(detailTagihanList: List<DetailTagihanModel>) {
        this.mDataset = detailTagihanList
        notifyDataSetChanged()
    }

    inner class DetailTagihanViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var relativeLayout: RelativeLayout = v.findViewById(R.id.relativeLayout)
        private var namaAnggota: TextView = v.findViewById(R.id.tvNama)
        private var statusAnggota: TextView = v.findViewById(R.id.tvStatus)
        private var jumlahBayar: TextView = v.findViewById(R.id.tvJmlTagihan)
        private var statusTagihan: TextView = v.findViewById(R.id.tvStatusTagihan)
        private var tvPrint: TextView = v.findViewById(R.id.tvPrint)
        private var print: View = v.findViewById(R.id.print)
        private var buttonOpsi: Button = v.findViewById(R.id.buttonOpsi)
        private var textInputLayoutBayar: TextInputLayout = v.findViewById(R.id.textInputLayoutBayar)
        private var textInputEditTextBayar: TextInputEditText = v.findViewById(R.id.textInputEditTextBayar)
        private val inputValidation : InputValidation = InputValidation(context!!)

        fun bindItem(detailTagihans: DetailTagihanModel){
            namaAnggota.text = detailTagihans.getNamaAnggota()
            context!!
            when {
                detailTagihans.getStatusAnggota()=="anggota" -> statusAnggota.text = context.getString(R.string.member)
                detailTagihans.getStatusAnggota()=="ketua" -> statusAnggota.text = context.getString(R.string.leader)
                detailTagihans.getStatusAnggota()=="bendahara" -> statusAnggota.text = context.getString(R.string.treasurer)
            }
            val jml: String = if (detailTagihans.getTotalBayar()!="null") {
                val formatter = DecimalFormat("#,###")
                val num = detailTagihans.getTotalBayar()!!.toInt()
                context.getString(R.string.rp) + " " + formatter.format(num).toString()
            }
            else context.getString(R.string.rp) + " 0"
            jumlahBayar.text = jml
            if (detailTagihans.getStatusTagihan()=="belum lunas") {
                onRefreshViewListner.addCount()
                statusTagihan.text = context.getString(R.string.not_paid)
                statusTagihan.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            }
            else if (detailTagihans.getStatusTagihan()=="lunas") {
                statusTagihan.text = context.getString(R.string.already_paid)
                statusTagihan.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            }
            if (title==context.getString(R.string.tab_make_bill)){
                var check = false
                relativeLayout.setOnClickListener {
                    if (detailTagihans.getStatusTagihan()!="lunas"){
                        if (!check){
                            buttonOpsi.visibility = View.VISIBLE
                            textInputLayoutBayar.visibility = View.VISIBLE
                            check = true
                        } else if (check){
                            buttonOpsi.visibility = View.GONE
                            textInputLayoutBayar.visibility = View.GONE
                            check = false
                        }
                    }
                }
                if (detailTagihans.getStatusTagihan()=="lunas") {
                    tvPrint.visibility = View.VISIBLE
                    print.visibility = View.VISIBLE
                    tvPrint.setOnClickListener {
                        printLaporan(detailTagihans.getNamaAnggota())
                    }
                    print.setOnClickListener {
                        printLaporan(detailTagihans.getNamaAnggota())
                    }
                }
            }
            buttonOpsi.setOnClickListener {
                if (!inputValidation.isInputEditTextFilled(textInputEditTextBayar, textInputLayoutBayar, context.getString(R.string.error_message_bills))) return@setOnClickListener
                val total: Int = if (detailTagihans.getTotalBayar().toString() == "null") 0
                else detailTagihans.getTotalBayar()!!.toInt()
                val kurang = tagihanModel.getJumlahTagihan()!!.toInt()-total
                val bayar = textInputEditTextBayar.text.toString().toInt()
                if (bayar>kurang) {
                    textInputLayoutBayar.error = context.getString(R.string.error_message_big) + " " + kurang.toString()
                    return@setOnClickListener
                }
                if (bayar==0){
                    textInputLayoutBayar.error = context.getString(R.string.error_message_zero)
                    return@setOnClickListener
                }
                AppUtils.progressDialog(context, context.getString(R.string.recording_payment))
                val queue = Volley.newRequestQueue(context)
                val stringRequest = object : StringRequest(Method.POST, Config.UPDATE_BILL_URL,
                    Response.Listener<String> { response ->
                        AppUtils.hideProgressDialog()
                        if (response.contains(Config.SUCCESS)){
                            textInputEditTextBayar.text = null
                            Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_SHORT).show()
                        } else Toast.makeText(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show()
                        onRefreshViewListner.refreshView()
                    },
                    Response.ErrorListener {
                        AppUtils.hideProgressDialog()
                        Toast.makeText(context, context.getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
                    }){
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params[Config.ID_ANGGOTA_SHARED_PREF] = detailTagihans.getIdAnggota().toString()
                        params[Config.ID_TAGIHAN_PARAM] = tagihanModel.getIdTagihan().toString()
                        params[Config.JUMLAH_BAYAR_PARAM] = textInputEditTextBayar.text.toString()
                        return params
                    }
                }
                queue.add(stringRequest)
                notifyDataSetChanged()
            }
        }
        private fun printLaporan(nama : String?) {
            context!!
            val uri = Uri.parse(Config.CETAK_KWITANSI)
                .buildUpon()
                .appendQueryParameter("nama", nama)
                .appendQueryParameter("uang", tagihanModel.getJumlahTagihan())
                .appendQueryParameter("untuk", tagihanModel.getNamaTagihan())
                .appendQueryParameter("bendahara", AppUtils.getName(context))
                .build().toString()
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            context.startActivity(browserIntent)
        }

    }

}