package com.zeecodeku.sitakesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.zeecodeku.sitakesi.helpers.InputValidation
import com.zeecodeku.sitakesi.utils.AppUtils
import com.zeecodeku.sitakesi.utils.Config
import java.util.ArrayList
import java.util.HashMap

class CreateLaporanActivity : AppCompatActivity(), View.OnClickListener {

    private val activity = this@CreateLaporanActivity

    private lateinit var spinnerType: AppCompatSpinner
    private lateinit var textInputLayoutJml: TextInputLayout
    private lateinit var textInputEditTextJml: TextInputEditText
    private lateinit var textInputLayoutKet: TextInputLayout
    private lateinit var textInputEditTextKet: TextInputEditText
    private lateinit var appCompatButtonCreate: AppCompatButton

    private lateinit var inputValidation: InputValidation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_laporan)

        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)

        // initializing the views
        initViews()

        // initializing the listeners
        initListeners()

        // initializing the objects
        initObjects()
    }

    private fun initViews() {
        spinnerType = findViewById(R.id.spinnerType)
        textInputLayoutJml = findViewById(R.id.textInputLayoutJml)
        textInputEditTextJml = findViewById(R.id.textInputEditTextJml)
        textInputLayoutKet = findViewById(R.id.textInputLayoutKet)
        textInputEditTextKet = findViewById(R.id.textInputEditTextKet)
        appCompatButtonCreate = findViewById(R.id.appCompatButtonCreate)
        spinner()
    }

    private fun initListeners() {
        appCompatButtonCreate.setOnClickListener(this)
    }

    private fun initObjects() {
        inputValidation = InputValidation(activity)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.appCompatButtonCreate -> createReport()
        }
    }

    private fun spinner() {
        val list = ArrayList<String>()
        list.add(getString(R.string.income))
        list.add(getString(R.string.spending))
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter
    }

    private fun createReport() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextKet, textInputLayoutKet, getString(R.string.error_message_report))) { return }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextJml, textInputLayoutJml, getString(R.string.error_message_amount))) { return }
        AppUtils.progressDialog(activity, getString(R.string.add_report))

        val idOrganisasi = intent.getStringExtra(Config.ID_ORGANISASI_PARAM).toString().trim()
        val type = spinnerType.selectedItem.toString()
        var jenisLaporan = ""
        if (type==getString(R.string.income)) jenisLaporan = "pemasukan"
        else if (type==getString(R.string.spending)) jenisLaporan = "pengeluaran"
        val jumlah = textInputEditTextJml.text.toString().trim()
        val ketLaporan = textInputEditTextKet.text.toString().trim()

        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, Config.INSERT_LAPORAN_URL, Response.Listener { response ->
                if (response.contains(Config.SUCCESS)) {
                    emptyInputEditText()
                    AppUtils.hideProgressDialog()
                    Toast.makeText(applicationContext, getString(R.string.success_message_laporan), Toast.LENGTH_LONG).show()
                    finish()
                }
            },
            Response.ErrorListener {
                AppUtils.hideProgressDialog()
                Toast.makeText(activity, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params[Config.ID_ORGANISASI_PARAM] = idOrganisasi
                params[Config.JENIS_LAPORAN_PARAM] = jenisLaporan
                params[Config.JUMLAH_PARAM] = jumlah
                params[Config.KET_LAPORAN_PARAM] = ketLaporan
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun emptyInputEditText() {
        textInputEditTextJml.text = null
        textInputEditTextKet.text = null
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
