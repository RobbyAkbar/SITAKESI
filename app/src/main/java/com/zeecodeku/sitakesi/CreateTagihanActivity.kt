package com.zeecodeku.sitakesi

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.*
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.zeecodeku.sitakesi.helpers.InputValidation
import com.zeecodeku.sitakesi.utils.AppUtils
import com.zeecodeku.sitakesi.utils.Config
import java.text.SimpleDateFormat
import java.util.*

class CreateTagihanActivity : AppCompatActivity(), View.OnClickListener {

    private val activity = this@CreateTagihanActivity

    private lateinit var textInputLayoutNamaTagihan: TextInputLayout
    private lateinit var textInputEditTextNamaTagihan: TextInputEditText
    private lateinit var textInputLayoutJumlahTagihan: TextInputLayout
    private lateinit var textInputEditTextJumlahTagihan: TextInputEditText
    private lateinit var tvDate: AppCompatTextView
    private lateinit var btnChoose: AppCompatImageButton
    private lateinit var btnCreate: AppCompatButton

    private var cal = Calendar.getInstance()
    private lateinit var inputValidation: InputValidation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_tagihan)

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
        textInputLayoutNamaTagihan = findViewById(R.id.textInputLayoutNmTagihan)
        textInputEditTextNamaTagihan = findViewById(R.id.textInputEditTextNmTagihan)
        textInputLayoutJumlahTagihan = findViewById(R.id.textInputLayoutJmlTagihan)
        textInputEditTextJumlahTagihan = findViewById(R.id.textInputEditTextJmlTagihan)
        tvDate = findViewById(R.id.tvDate)
        btnChoose = findViewById(R.id.appCompatButtonChoose)
        btnCreate = findViewById(R.id.appCompatButtonCreate)
    }

    private fun initListeners() {
        btnChoose.setOnClickListener(this)
        btnCreate.setOnClickListener(this)
    }

    private fun initObjects() {
        inputValidation = InputValidation(activity)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.appCompatButtonChoose -> {
                val mDate = DatePickerDialog(activity, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                mDate.datePicker.minDate = System.currentTimeMillis() - 1000
                mDate.show()
            }
            R.id.appCompatButtonCreate -> createBill()
        }
    }

    private val dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
        datePicker.minDate = System.currentTimeMillis() - 1000
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        tvDate.text = sdf.format(cal.time)
        tvDate.setTextColor(ContextCompat.getColor(activity, R.color.colorBackground))
    }

    private fun createBill() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextNamaTagihan, textInputLayoutNamaTagihan, getString(R.string.error_message_bill))) {
            return
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextJumlahTagihan, textInputLayoutJumlahTagihan, getString(R.string.error_message_total_bill))) {
            return
        }
        if (tvDate.text == "xxxx-xx-xx"){
            tvDate.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark))
            return
        }
        AppUtils.progressDialog(activity, getString(R.string.add_bill))
        val namaTagihan = textInputEditTextNamaTagihan.text.toString().trim()
        val jumlahTagihan = textInputEditTextJumlahTagihan.text.toString().trim()
        val dtDeadline = tvDate.text.toString().trim()
        val idOrganisasi = intent.getStringExtra(Config.ID_ORGANISASI_PARAM).toString().trim()

        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, Config.INSERT_TAGIHAN_URL, Response.Listener { response ->
                if (response.contains(Config.SUCCESS)) {
                    emptyInputEditText()
                    AppUtils.hideProgressDialog()
                    Toast.makeText(applicationContext, getString(R.string.success_message_tagihan), Toast.LENGTH_LONG).show()
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
                params[Config.NAMA_TAGIHAN_PARAM] = namaTagihan
                params[Config.JUMLAH_TAGIHAN_PARAM] = jumlahTagihan
                params[Config.DT_DEADLINE_PARAM] = dtDeadline
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun emptyInputEditText() {
        textInputEditTextNamaTagihan.text = null
        textInputEditTextJumlahTagihan.text = null
        tvDate.text = getString(R.string.xxxx_xx_xx)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}
