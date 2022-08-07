package com.zeecodeku.sitakesi.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.zeecodeku.sitakesi.R
import com.zeecodeku.sitakesi.adapters.AnggotaAdapter
import com.zeecodeku.sitakesi.models.AnggotaModel
import com.zeecodeku.sitakesi.parsing.AnggotaJSONParser
import com.zeecodeku.sitakesi.utils.Config

/**
 * A simple [Fragment] subclass.
 *
 */
class PilihBendaharaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var anggotaAdapter: AnggotaAdapter
    private lateinit var args: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        args = requireArguments().getString("args", "")
        anggotaAdapter = AnggotaAdapter(args, context)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.recycler_view, container, false)

        // initializing the views
        initViews(view)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        recyclerView.adapter = anggotaAdapter

        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun getData() {
        progressBar.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(Method.POST, Config.LIST_ANGGOTA_URL,
            Response.Listener { response ->
                val anggotaList: List<AnggotaModel> = AnggotaJSONParser.parseData(response)
                anggotaAdapter.updateWith(anggotaList)
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

}
