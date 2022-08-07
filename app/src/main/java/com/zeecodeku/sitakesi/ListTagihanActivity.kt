package com.zeecodeku.sitakesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.zeecodeku.sitakesi.adapters.DetailTagihanAdapter
import com.zeecodeku.sitakesi.listeners.OnRefreshViewListner
import com.zeecodeku.sitakesi.models.DetailTagihanModel
import com.zeecodeku.sitakesi.models.TagihanModel
import com.zeecodeku.sitakesi.parsing.DetailTagihanJSONParser
import com.zeecodeku.sitakesi.utils.AppUtils
import com.zeecodeku.sitakesi.utils.Config

class ListTagihanActivity : AppCompatActivity(), OnRefreshViewListner {

    override fun addCount() {
        count += 1
    }

    private val activity = this@ListTagihanActivity

    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tagihanModel: TagihanModel
    private lateinit var actionBar: ActionBar
    private lateinit var detailTagihanAdapter: DetailTagihanAdapter
    private lateinit var title: String
    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_view)

        // initializing the views
        initViews()

        // initializing the objects
        initObjects()

        title = intent.getStringExtra("title")!!
        detailTagihanAdapter = DetailTagihanAdapter(tagihanModel, title, activity)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = detailTagihanAdapter

        getData()

        if (title==getString(R.string.tab_make_bill)) Snackbar.make(coordinatorLayout, getString(R.string.record_payment), Snackbar.LENGTH_SHORT).show()
        //else if (title==getString(R.string.tab_bill)) Snackbar.make(coordinatorLayout, getString(R.string.see_detail), Snackbar.LENGTH_SHORT).show()
    }

    /**
     * This method is to initialize views
     */
    private fun initViews() {
        coordinatorLayout = findViewById(R.id.coordinator_layout)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        tagihanModel = intent.getParcelableExtra(Config.EXTRA_TAGIHAN)!!
        actionBar = supportActionBar!!
    }

    /**
     * This method is to initialize objects to be used
     */
    private fun initObjects() {
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = tagihanModel.getNamaTagihan()
        actionBar.subtitle = getString(R.string.pay_limit)+" "+tagihanModel.getDtDeadline()
    }

    override fun refreshView() {
        getData()
    }

    private fun getData() {
        progressBar.visibility = View.VISIBLE
        val idTagihan = tagihanModel.getIdTagihan().toString()
        val idOrganisasi = intent.getStringExtra(Config.ID_ORGANISASI_PARAM)!!
        val queue = Volley.newRequestQueue(activity)
        val stringRequest = object : StringRequest(Method.POST, Config.LIST_DETAIL_TAGIHAN_URL,
            Response.Listener { response ->
                val detailList: List<DetailTagihanModel> = DetailTagihanJSONParser.parseData(response)
                detailTagihanAdapter.updateWith(detailList)
                progressBar.visibility = View.INVISIBLE
            },
            Response.ErrorListener {
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(activity, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params[Config.ID_TAGIHAN_PARAM] = idTagihan
                params[Config.ID_ORGANISASI_PARAM] = idOrganisasi
                return params
            }
        }
        queue.add(stringRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        if (title==getString(R.string.tab_make_bill)) inflater.inflate(R.menu.menu_selesai, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.done -> {
                if (count>0){
                    Snackbar.make(coordinatorLayout, getString(R.string.not_all_pay), Snackbar.LENGTH_SHORT).show()
                    return true
                }
                val builder = AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                builder.setMessage(getString(R.string.message_finish))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        AppUtils.progressDialog(activity, getString(R.string.settling_bill))
                        val queue = Volley.newRequestQueue(activity)
                        val stringRequest = object : StringRequest(Method.POST, Config.SET_BILL_URL,
                            Response.Listener { response ->
                                if (response.contains(Config.SUCCESS)){
                                    Toast.makeText(activity, getString(R.string.success_update), Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                AppUtils.hideProgressDialog()
                            },
                            Response.ErrorListener {
                                AppUtils.hideProgressDialog()
                                Toast.makeText(activity, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
                            }){
                            override fun getParams(): Map<String, String> {
                                val params = HashMap<String, String>()
                                params[Config.ID_TAGIHAN_PARAM] = tagihanModel.getIdTagihan().toString()
                                return params
                            }
                        }
                        queue.add(stringRequest)
                    }
                    .setNegativeButton(
                        getString(R.string.no)
                    ) { dialogInterface, _ -> dialogInterface.cancel() }
                    .show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
