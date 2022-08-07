package com.zeecodeku.sitakesi.adapters

import android.content.Context
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zeecodeku.sitakesi.R
import com.zeecodeku.sitakesi.models.TagihanModel
import java.text.DecimalFormat

class TagihanAdapter (
    private val context: Context?,
    internal var recyclerViewItemClickListener: RecyclerViewItemClickListener
): RecyclerView.Adapter<TagihanAdapter.TagihanViewHolder>() {

    private var mDataset: List<TagihanModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): TagihanViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_tagihan, parent, false)

        return TagihanViewHolder(v)
    }

    override fun onBindViewHolder(tagihanViewHolder: TagihanViewHolder, i: Int) {
        tagihanViewHolder.bindItem(mDataset[i])
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    fun updateWith(tagihanList: List<TagihanModel>) {
        this.mDataset = tagihanList
        notifyDataSetChanged()
    }

    inner class TagihanViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var namaTagihan: TextView = v.findViewById(R.id.tvNamaTagihan)
        private var jumlahTagihan: TextView = v.findViewById(R.id.tvJmlTagihan)
        private var dtDeadline: TextView = v.findViewById(R.id.tvDate)
        private var statusTagihan: TextView = v.findViewById(R.id.tvStatusTagihan)

        fun bindItem(tagihans: TagihanModel){
            namaTagihan.text = tagihans.getNamaTagihan()
            context!!

            val formatter = DecimalFormat("#,###")
            val num = tagihans.getJumlahTagihan()!!.toInt()

            val jml = context.getString(R.string.rp) + " " + formatter.format(num).toString()

            jumlahTagihan.text = jml
            dtDeadline.text = tagihans.getDtDeadline()
            if (tagihans.getStatusTagihan()=="belum selesai") {
                statusTagihan.text = context.getString(R.string.unfinished)
                statusTagihan.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            }
            else if (tagihans.getStatusTagihan()=="selesai") {
                statusTagihan.text = context.getString(R.string.finished)
                statusTagihan.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            }
        }

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            recyclerViewItemClickListener.clickOnItem(mDataset[this.adapterPosition])
        }
    }

    interface RecyclerViewItemClickListener {
        fun clickOnItem(data: TagihanModel)
    }

}
