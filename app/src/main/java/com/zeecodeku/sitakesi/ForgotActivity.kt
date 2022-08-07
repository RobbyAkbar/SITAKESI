package com.zeecodeku.sitakesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.zeecodeku.sitakesi.helpers.InputValidation
import com.zeecodeku.sitakesi.utils.AppUtils
import com.zeecodeku.sitakesi.utils.Config
import java.util.HashMap

class ForgotActivity : AppCompatActivity(), View.OnClickListener {

    private val activity = this@ForgotActivity

    private lateinit var nestedScrollView: NestedScrollView

    private lateinit var textInputLayoutEmail: TextInputLayout

    private lateinit var textInputEditTextEmail: TextInputEditText

    private lateinit var appCompatButtonReset: AppCompatButton

    private lateinit var textViewLinkLogin: AppCompatTextView

    private lateinit var inputValidation: InputValidation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_forgot)

        // hiding the action bar
        supportActionBar!!.hide()

        // initializing the views
        initViews()

        // initializing the listeners
        initListeners()

        // initializing the objects
        initObjects()
    }

    /**
     * This method is to initialize views
     */
    private fun initViews() {

        nestedScrollView = findViewById<View>(R.id.nestedScrollView) as NestedScrollView

        textInputLayoutEmail = findViewById<View>(R.id.textInputLayoutEmail) as TextInputLayout

        textInputEditTextEmail = findViewById<View>(R.id.textInputEditTextEmail) as TextInputEditText

        appCompatButtonReset = findViewById<View>(R.id.appCompatButtonReset) as AppCompatButton

        textViewLinkLogin = findViewById<View>(R.id.appCompatTextViewLoginLink) as AppCompatTextView

    }

    /**
     * This method is to initialize listeners
     */
    private fun initListeners() {

        appCompatButtonReset.setOnClickListener(this)
        textViewLinkLogin.setOnClickListener(this)
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
            R.id.appCompatButtonLogin -> resetPassword()
            R.id.appCompatTextViewLoginLink -> finish()
        }
    }

    /**
     * This method is to validate the input text fields and reset password
     */
    private fun resetPassword() {

        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_email))) {
            return
        }

        AppUtils.progressDialog(activity, activity.getString(R.string.reset))
        val email = textInputEditTextEmail.text.toString().trim()

        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, Config.RESET_URL,
            Response.Listener { response ->
                when {
                    response.contains(Config.SUCCESS) -> {
                        emptyInputEditText()
                        AppUtils.hideProgressDialog()
                        Snackbar.make(nestedScrollView, getString(R.string.success_reset), Snackbar.LENGTH_LONG).show()
                    }
                    response.contains(Config.NO_USER) -> {
                        AppUtils.hideProgressDialog()
                        Snackbar.make(nestedScrollView, getString(R.string.error_no_user), Snackbar.LENGTH_LONG).show()
                    }
                    response.contains(Config.INVALID_EMAIL) -> {
                        AppUtils.hideProgressDialog()
                        Snackbar.make(nestedScrollView, getString(R.string.error_invalid_email), Snackbar.LENGTH_LONG).show()
                    }
                }
            },
            Response.ErrorListener {
                AppUtils.hideProgressDialog()
                Snackbar.make(nestedScrollView, getString(R.string.connection_failed), Snackbar.LENGTH_LONG).show()
            }){
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params[Config.EMAIL_SHARED_PREF] = email
                return params
            }
        }
        queue.add(stringRequest)

    }

    /**
     * This method is to empty all input edit text
     */
    private fun emptyInputEditText() {
        textInputEditTextEmail.text = null
    }}
