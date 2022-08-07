package com.zeecodeku.sitakesi.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.zeecodeku.sitakesi.ListTagihanActivity

import com.zeecodeku.sitakesi.R
import com.zeecodeku.sitakesi.adapters.TagihanAdapter
import com.zeecodeku.sitakesi.models.TagihanModel
import com.zeecodeku.sitakesi.parsing.TagihanJSONParser
import com.zeecodeku.sitakesi.utils.AppUtils
import com.zeecodeku.sitakesi.utils.Config

/**
 * A simple [Fragment] subclass.
 *
 */
class TagihanFragment : Fragment(), TagihanAdapter.RecyclerViewItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoData: TextView
    private lateinit var tagihanAdapter: TagihanAdapter
    private lateinit var args: String
    private lateinit var title: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (AppUtils.getTagihan(args, requireContext()) != "") setRecyclerView(AppUtils.getTagihan(args, requireActivity())!!)
        if (AppUtils.isNetworkStatusAvialable(requireActivity())) getData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        title = requireArguments().getString("title", "")
        args = requireArguments().getString("args", "")
        tagihanAdapter = TagihanAdapter(activity, this)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.recycler_view, container, false)

        initViews(view)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = tagihanAdapter

        return  view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        tvNoData = view.findViewById(R.id.tvNoData)
    }

    private fun getData() {
        progressBar.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(Method.POST, Config.LIST_TAGIHAN_URL,
            Response.Listener { response ->
                if (response.contains(Config.FAILURE)) {
                    tvNoData.visibility = View.VISIBLE
                }
                else {
                    tvNoData.visibility = View.INVISIBLE
                    setRecyclerView(response)
                    val sharedPreferences =
                        requireActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString(Config.TAGIHANJSON_SHARED_PREF+args, response)
                    editor.apply()
                }
                progressBar.visibility = View.INVISIBLE
            },
            Response.ErrorListener {
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(activity, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params[Config.ID_ORGANISASI_PARAM] = args
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun setRecyclerView(response: String){
        val tagihanList: List<TagihanModel> = TagihanJSONParser.parseData(response)
        tagihanAdapter.updateWith(tagihanList)
    }

    override fun clickOnItem(data: TagihanModel) {
        val intentTagihan = Intent(activity, ListTagihanActivity::class.java)
        intentTagihan.putExtra(Config.EXTRA_TAGIHAN, data)
        intentTagihan.putExtra(Config.ID_ORGANISASI_PARAM, args)
        intentTagihan.putExtra("title", title)
        if (data.getStatusTagihan()=="belum selesai"){
            if (AppUtils.isNetworkStatusAvialable(requireActivity())) startActivity(intentTagihan)
            else Toast.makeText(activity, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
        }
        else Toast.makeText(activity, getString(R.string.message_complete), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        getData()
        return super.onResume()
    }

}
