package com.zeecodeku.sitakesi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zeecodeku.sitakesi.R
import com.zeecodeku.sitakesi.models.OrganisasiModel

class OrganisasiAdapter (
    internal var recyclerViewItemClickListener: RecyclerViewItemClickListener
): RecyclerView.Adapter<OrganisasiAdapter.OrganisasiViewHolder>() {

    private var mDataset: List<OrganisasiModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): OrganisasiViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_organisasi, parent, false)

        return OrganisasiViewHolder(v)
    }

    override fun onBindViewHolder(organisasiViewHolder: OrganisasiViewHolder, i: Int) {
        organisasiViewHolder.bindItem(mDataset[i])
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    fun updateWith(organisasiList: List<OrganisasiModel>) {
        this.mDataset = organisasiList
        notifyDataSetChanged()
    }

    inner class OrganisasiViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private var title: TextView = v.findViewById(R.id.card_title) as TextView
        private var content: TextView = v.findViewById(R.id.card_content) as TextView
        private var opsi: TextView = v.findViewById(R.id.card_admin) as TextView
        private var status: TextView = v.findViewById(R.id.card_status) as TextView

        fun bindItem(organizations: OrganisasiModel){
            title.text = organizations.getNamaOrganisasi()
            content.text = organizations.getNamaInstansi()
            if (organizations.getStatusOrganisasi()=="nonaktif") status.visibility = View.VISIBLE
            else if (organizations.getStatusAnggota()=="ketua") opsi.visibility = View.VISIBLE
        }

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            recyclerViewItemClickListener.clickOnItem(mDataset[this.adapterPosition])
        }
    }

    interface RecyclerViewItemClickListener {
        fun clickOnItem(data: OrganisasiModel)
    }

}
