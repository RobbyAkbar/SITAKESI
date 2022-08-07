package com.zeecodeku.sitakesi

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.tabs.TabLayoutMediator
import com.zeecodeku.sitakesi.adapters.ViewPagerAdapter
import com.zeecodeku.sitakesi.fragments.TagihanFragment
import com.zeecodeku.sitakesi.fragments.LaporanFragment
import com.zeecodeku.sitakesi.fragments.PilihBendaharaFragment
import com.zeecodeku.sitakesi.models.OrganisasiModel
import com.zeecodeku.sitakesi.utils.Config
import java.text.DecimalFormat
import java.util.HashMap

class OrganizationActivity : AppCompatActivity() {

    private val activity = this@OrganizationActivity

    private lateinit var toolbar: Toolbar
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private val viewPagerAdapter = ViewPagerAdapter(activity)
    private lateinit var organisasiModel: OrganisasiModel
    private lateinit var floatingActionButton: FloatingActionButton
    private var menu: String = ""

    private lateinit var print: View

    private lateinit var tvPrint: TextView
    private lateinit var tvBill: TextView
    private lateinit var tvIncome: TextView
    private lateinit var tvSpending: TextView
    private lateinit var tvTotal: TextView

    private lateinit var clipboardManager: ClipboardManager
    private lateinit var clipData: ClipData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization)

        // initializing the views
        initViews()

        // initializing the listeners
        initListeners()

        if (organisasiModel.getStatusAnggota()!="bendahara"){
            viewPagerAdapter.addFragment(TagihanFragment(), organisasiModel.getIdOrganisasi().toString(), getString(R.string.tab_bill), 0)
            viewPagerAdapter.addFragment(LaporanFragment(), organisasiModel.getIdOrganisasi().toString(), getString(R.string.tab_report), 1)
        }

        if (organisasiModel.getStatusAnggota().equals("ketua"))
            viewPagerAdapter.addFragment(PilihBendaharaFragment(), organisasiModel.getIdOrganisasi().toString(), getString(R.string.tab_select), 2)
        else if (organisasiModel.getStatusAnggota().equals("bendahara")){
            viewPagerAdapter.addFragment(TagihanFragment(), organisasiModel.getIdOrganisasi().toString(), getString(R.string.tab_make_bill), 0)
            viewPagerAdapter.addFragment(LaporanFragment(), organisasiModel.getIdOrganisasi().toString(), getString(R.string.tab_make_report), 1)
        }

        // initializing the objects
        initObjects()

        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)

        getDana()
    }

    /**
     * This method is to initialize views
     */
    private fun initViews() {
        toolbar = findViewById(R.id.tab_toolbar)
        coordinatorLayout = findViewById(R.id.tab_coordinator_layout)
        viewPager = findViewById(R.id.tab_view_pager)
        tabLayout = findViewById(R.id.tab_layout)
        organisasiModel = intent.getParcelableExtra(Config.EXTRA_ORGANIZATION)!!
        floatingActionButton = findViewById(R.id.fab)
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        print = findViewById(R.id.print)
        tvPrint = findViewById(R.id.tvPrint)
        tvBill = findViewById(R.id.bill)
        tvIncome = findViewById(R.id.income)
        tvSpending = findViewById(R.id.spending)
        tvTotal = findViewById(R.id.total)
    }

    /**
     * This method is to initialize listeners
     */
    private fun initListeners() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    getString(R.string.tab_make_bill) -> {
                        floatingActionButton.show()
                        menu = "bill"
                    }
                    getString(R.string.tab_make_report) -> {
                        floatingActionButton.show()
                        menu = "report"
                    }
                    getString(R.string.tab_bill) -> floatingActionButton.hide()
                    getString(R.string.tab_report) -> floatingActionButton.hide()
                    getString(R.string.tab_select) -> floatingActionButton.hide()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        floatingActionButton.setOnClickListener {
            lateinit var intentCreate: Intent
            if (menu=="report") intentCreate = Intent(applicationContext, CreateLaporanActivity::class.java)
            else if (menu=="bill") intentCreate = Intent(applicationContext, CreateTagihanActivity::class.java)
            intentCreate.putExtra(Config.ID_ORGANISASI_PARAM, organisasiModel.getIdOrganisasi())
            startActivity(intentCreate)
        }
        print.setOnClickListener {
            printLaporan()
        }
        tvPrint.setOnClickListener {
            printLaporan()
        }
    }

    private fun printLaporan() {
        val uri = Uri.parse(Config.CETAK_LAPORAN)
            .buildUpon()
            .appendQueryParameter(Config.ID_ORGANISASI_PARAM, organisasiModel.getIdOrganisasi())
            .build().toString()
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(browserIntent)
    }

    /**
     * This method is to initialize objects to be used
     */
    private fun initObjects() {
        toolbar.title = organisasiModel.getNamaOrganisasi()
        toolbar.subtitle = organisasiModel.getNamaInstansi()
        viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = viewPagerAdapter.getPageTitle(position)
        }.attach()

        //tabLayout.setupWithViewPager(viewPager)
        tabLayout.post(tabLayoutConfig)
        setSupportActionBar(toolbar)
    }

    private var tabLayoutConfig: Runnable = Runnable {
        if (tabLayout.width < activity.resources.displayMetrics.widthPixels) {
            tabLayout.tabMode = TabLayout.MODE_FIXED
            val mParams = tabLayout.layoutParams
            mParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            tabLayout.layoutParams = mParams

        } else {
            tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_info, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.info -> {
                info()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getDana() {
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, Config.GET_DANA_URL, Response.Listener { response ->
                val data = response.split("~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val formatter = DecimalFormat("#,###")
                val jml = arrayOfNulls<String>(4)
                val total = data[0].toInt()+data[1].toInt()-data[2].toInt()
                for (i in 0 until 3) {
                    jml[i] = getString(R.string.rp) + " " + formatter.format(data[i].toInt()).toString()
                }
                jml[3] = getString(R.string.rp) + " " + formatter.format(total).toString()
                tvBill.text = jml[0]
                tvIncome.text = jml[1]
                tvSpending.text = jml[2]
                tvTotal.text = jml[3]
            },
            Response.ErrorListener {
            }){
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params[Config.ID_ORGANISASI_PARAM] = organisasiModel.getIdOrganisasi().toString()
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun info() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.detail_organization))
        builder.setMessage(getString(R.string.referral_code)+" : "+organisasiModel.getReferral()+"\n"+
                getString(R.string.date_join)+" : "+organisasiModel.getDtBuat()+"\n"+
                getString(R.string.date_active)+" : "+organisasiModel.getDtAktif()
        )
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setNeutralButton(getString(R.string.copy_referral)) { dialog, _ ->
            clipData = ClipData.newPlainText("referral", organisasiModel.getReferral())
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(activity, getString(R.string.referral_copied), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onResume() {
        getDana()
        super.onResume()
    }

}
