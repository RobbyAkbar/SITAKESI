package com.zeecodeku.sitakesi.adapters

import android.content.Context
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zeecodeku.sitakesi.R
import com.zeecodeku.sitakesi.models.LaporanModel
import java.text.DecimalFormat

class LaporanAdapter (
    private val context: Context?
): RecyclerView.Adapter<LaporanAdapter.LaporanViewHolder>() {

    private var mDataset: List<LaporanModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): LaporanViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_tagihan, parent, false)

        return LaporanViewHolder(v)
    }

    override fun onBindViewHolder(laporanViewHolder: LaporanViewHolder, i: Int) {
        laporanViewHolder.bindItem(mDataset[i])
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    fun updateWith(laporanList: List<LaporanModel>) {
        this.mDataset = laporanList
        notifyDataSetChanged()
    }

    inner class LaporanViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var namaLaporan: TextView = v.findViewById(R.id.tvNamaTagihan)
        private var jumlah: TextView = v.findViewById(R.id.tvJmlTagihan)
        private var text: TextView = v.findViewById(R.id.tvLimit)
        private var dtBuat: TextView = v.findViewById(R.id.tvDate)
        private var jenisLaporan: TextView = v.findViewById(R.id.tvStatusTagihan)

        fun bindItem(laporans: LaporanModel){
            context!!
            namaLaporan.text = laporans.getKetLaporan()

            val formatter = DecimalFormat("#,###")
            val num = laporans.getJumlah().toInt()
            val jml = context.getString(R.string.rp) + " " + formatter.format(num).toString()
            jumlah.text = jml

            text.text = context.getText(R.string.report)
            dtBuat.text = laporans.getDtBuat()

            if (laporans.getJenisLaporan()=="pengeluaran") {
                jenisLaporan.text = context.getString(R.string.spending)
                jenisLaporan.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            }
            else if (laporans.getJenisLaporan()=="pemasukan") {
                jenisLaporan.text = context.getString(R.string.income)
                jenisLaporan.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            }

        }
    }

}