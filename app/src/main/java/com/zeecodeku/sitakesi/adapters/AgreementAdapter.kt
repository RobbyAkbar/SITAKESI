package com.zeecodeku.sitakesi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zeecodeku.sitakesi.R
import com.zeecodeku.sitakesi.models.AgreementModel

class AgreementAdapter : RecyclerView.Adapter<AgreementAdapter.AgreementViewHolder>() {

    private var mDataset: List<AgreementModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): AgreementViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_agreement, parent, false)

        return AgreementViewHolder(v)
    }

    override fun onBindViewHolder(agreementViewHolder: AgreementViewHolder, i: Int) {
        agreementViewHolder.bindItem(mDataset[i])
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    fun updateWith(agreeList: List<AgreementModel>) {
        this.mDataset = agreeList
        notifyDataSetChanged()
    }

    inner class AgreementViewHolder(v: View) : RecyclerView.ViewHolder(v){
        private var no: TextView = v.findViewById(R.id.no) as TextView
        private var agree: TextView = v.findViewById(R.id.agreement) as TextView

        fun bindItem(agrees: AgreementModel){
            no.text = agrees.getAgreeId()
            agree.text = agrees.getAgreeName()
        }
    }
}
