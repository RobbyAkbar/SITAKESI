package com.zeecodeku.sitakesi

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.zeecodeku.sitakesi.utils.Config.ALLOWED_CHARACTERS
import com.zeecodeku.sitakesi.adapters.AgreementAdapter
import com.zeecodeku.sitakesi.helpers.InputValidation
import com.zeecodeku.sitakesi.parsing.AgreementJSONParser
import com.zeecodeku.sitakesi.utils.AppUtils
import com.zeecodeku.sitakesi.utils.Config
import java.util.*

class CreateOrganisasiActivity : AppCompatActivity(), View.OnClickListener {
    private val activity = this@CreateOrganisasiActivity

    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var textInputLayoutOrganization: TextInputLayout
    private lateinit var textInputLayoutAgency: TextInputLayout
    private lateinit var textInputEditTextOrganization: TextInputEditText
    private lateinit var textInputEditTextAgency: TextInputEditText

    private lateinit var textViewReferral: AppCompatTextView
    private lateinit var renew: View
    private lateinit var copy: View

    private lateinit var appCompatButtonCreate: AppCompatButton

    private lateinit var clipboardManager: ClipboardManager
    private lateinit var clipData: ClipData

    private lateinit var inputValidation: InputValidation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_organisasi)

        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)

        // initializing the views
        initViews()

        // initializing the listeners
        initListeners()

        // initializing the objects
        initObjects()

        textViewReferral.text = generateReferral()
    }

    /**
     * This method is to initialize views
     */
    private fun initViews() {
        constraintLayout = findViewById<View>(R.id.constraintLayout) as ConstraintLayout

        textInputLayoutOrganization = findViewById<View>(R.id.textInputLayoutOrganization) as TextInputLayout
        textInputLayoutAgency = findViewById<View>(R.id.textInputLayoutAgency) as TextInputLayout

        textInputEditTextOrganization = findViewById<View>(R.id.textInputEditTextOrganization) as TextInputEditText
        textInputEditTextAgency = findViewById<View>(R.id.textInputEditTextAgency) as TextInputEditText

        appCompatButtonCreate = findViewById<View>(R.id.appCompatButtonCreate) as AppCompatButton

        textViewReferral = findViewById<View>(R.id.textViewReferral) as AppCompatTextView
        renew = findViewById(R.id.renew)
        copy = findViewById(R.id.copy)

        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    /**
     * This method is to initialize listeners
     */
    private fun initListeners() {
        appCompatButtonCreate.setOnClickListener(this)
        renew.setOnClickListener(this)
        copy.setOnClickListener(this)
    }

    /**
     * This method is to initialize objects to be used
     */
    private fun initObjects() {
        inputValidation = InputValidation(activity)
    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.appCompatButtonCreate -> popUpAgreement()
            R.id.renew -> {
                textViewReferral.text = generateReferral()
            }
            R.id.copy -> {
                if (textViewReferral.text != ""){
                    clipData = ClipData.newPlainText("referral", textViewReferral.text)
                    clipboardManager.setPrimaryClip(clipData)
                    Snackbar.make(constraintLayout, R.string.referral_copied, Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(constraintLayout, R.string.referral_generate, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun generateReferral(): String  {
        val random = Random()
        val sb = StringBuilder(8)
        for (i in 0 until 8)
            sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
        return sb.toString()
    }

    private fun popUpAgreement() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextOrganization, textInputLayoutOrganization, getString(R.string.error_message_organization))) {
            return
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextAgency, textInputLayoutAgency, getString(R.string.error_message_agency))) {
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.tos)
        val dialogLayout = View.inflate(activity, R.layout.agreement_layout, null)
        val progressBar = dialogLayout.findViewById<ProgressBar>(R.id.progressBar)
        val agreementAdapter = AgreementAdapter()
        val recyclerView = dialogLayout.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = agreementAdapter
        builder.setView(dialogLayout)
        builder.setPositiveButton(R.string.ok) { _, _ -> }
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            dialog.dismiss()
            postData()
        }
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET, Config.AGREEMENT_URL,
            { response ->
                val agreementList = AgreementJSONParser.parseData(response)
                agreementAdapter.updateWith(agreementList)
                progressBar.visibility = View.GONE
            },
            {
                progressBar.visibility = View.GONE
                Toast.makeText(activity, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            })
        queue.add(stringRequest)
    }

    /**
     * This method is to validate the input text fields and post data
     */
    private fun postData() {
        AppUtils.progressDialog(activity, getString(R.string.creating_organization))
        val organization = textInputEditTextOrganization.text.toString().trim()
        val agency = textInputEditTextAgency.text.toString().trim()
        val referral = textViewReferral.text.toString()
        val idAnggota = AppUtils.getIdAnggota(activity)!!

        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, Config.CREATE_ORGANIZATION_URL,
            Response.Listener { response ->
                if (response.contains(Config.SUCCESS)) {
                    emptyInputEditText()
                    AppUtils.hideProgressDialog()
                    Toast.makeText(applicationContext, getString(R.string.success_message), Toast.LENGTH_LONG).show()
                    finish()
                }
            },
            Response.ErrorListener {
                AppUtils.hideProgressDialog()
                Toast.makeText(activity, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params[Config.ORGANISASI_PARAM] = organization
                params[Config.INSTANSI_PARAM] = agency
                params[Config.REFERRAL_PARAM] = referral
                params[Config.ID_ANGGOTA_SHARED_PREF] = idAnggota
                return params
            }
        }
        queue.add(stringRequest)
    }

    /**
     * This method is to empty all input edit text
     */
    private fun emptyInputEditText() {
        textInputEditTextOrganization.text = null
        textInputEditTextAgency.text = null
        textViewReferral.text = null
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
