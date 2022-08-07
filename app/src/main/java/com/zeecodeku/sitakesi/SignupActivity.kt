package com.zeecodeku.sitakesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.core.widget.NestedScrollView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import android.view.View
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.zeecodeku.sitakesi.helpers.InputValidation
import com.zeecodeku.sitakesi.utils.AppUtils
import com.zeecodeku.sitakesi.utils.Config
import java.util.HashMap

class SignupActivity : AppCompatActivity(), View.OnClickListener {

    private val activity = this@SignupActivity

    private lateinit var nestedScrollView: NestedScrollView

    private lateinit var textInputLayoutName: TextInputLayout
    private lateinit var textInputLayoutEmail: TextInputLayout
    private lateinit var textInputLayoutPassword: TextInputLayout
    private lateinit var textInputLayoutConfirmPassword: TextInputLayout

    private lateinit var textInputEditTextName: TextInputEditText
    private lateinit var textInputEditTextEmail: TextInputEditText
    private lateinit var textInputEditTextPassword: TextInputEditText
    private lateinit var textInputEditTextConfirmPassword: TextInputEditText

    private lateinit var appCompatButtonRegister: AppCompatButton
    private lateinit var appCompatTextViewLoginLink: AppCompatTextView

    private lateinit var inputValidation: InputValidation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

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

        textInputLayoutName = findViewById<View>(R.id.textInputLayoutName) as TextInputLayout
        textInputLayoutEmail = findViewById<View>(R.id.textInputLayoutEmail) as TextInputLayout
        textInputLayoutPassword = findViewById<View>(R.id.textInputLayoutPassword) as TextInputLayout
        textInputLayoutConfirmPassword = findViewById<View>(R.id.textInputLayoutConfirmPassword) as TextInputLayout

        textInputEditTextName = findViewById<View>(R.id.textInputEditTextName) as TextInputEditText
        textInputEditTextEmail = findViewById<View>(R.id.textInputEditTextEmail) as TextInputEditText
        textInputEditTextPassword = findViewById<View>(R.id.textInputEditTextPassword) as TextInputEditText
        textInputEditTextConfirmPassword = findViewById<View>(R.id.textInputEditTextConfirmPassword) as TextInputEditText

        appCompatButtonRegister = findViewById<View>(R.id.appCompatButtonRegister) as AppCompatButton

        appCompatTextViewLoginLink = findViewById<View>(R.id.appCompatTextViewLoginLink) as AppCompatTextView

    }

    /**
     * This method is to initialize listeners
     */
    private fun initListeners() {
        appCompatButtonRegister.setOnClickListener(this)
        appCompatTextViewLoginLink.setOnClickListener(this)

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

            R.id.appCompatButtonRegister -> postData()

            R.id.appCompatTextViewLoginLink -> finish()
        }
    }

    /**
     * This method is to validate the input text fields and post data
     */
    private fun postData() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextName, textInputLayoutName, getString(R.string.error_message_name))) {
            return
        }
        if (!inputValidation.isInputTextMinimum(textInputEditTextName, textInputLayoutName, 3)){
            return
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_email))) {
            return
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return
        }
        if (!inputValidation.isInputTextMinimum(textInputEditTextPassword, textInputLayoutPassword, 6)){
            return
        }
        if (!inputValidation.isInputEditTextMatches(textInputEditTextPassword, textInputEditTextConfirmPassword,
                textInputLayoutConfirmPassword, getString(R.string.error_password_match))) {
            return
        }

        AppUtils.progressDialog(activity, activity.getString(R.string.creating_account))
        val name = textInputEditTextName.text.toString().trim()
        val email = textInputEditTextEmail.text.toString().trim()
        val password = textInputEditTextPassword.text.toString().trim()

        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, Config.SIGNUP_URL,
            Response.Listener {
                emptyInputEditText()
                AppUtils.hideProgressDialog()
                Snackbar.make(nestedScrollView, getString(R.string.success_message), Snackbar.LENGTH_LONG).show()
            },
            Response.ErrorListener {
                AppUtils.hideProgressDialog()
                Snackbar.make(nestedScrollView, getString(R.string.connection_failed), Snackbar.LENGTH_LONG).show()
            }){
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params[Config.NAMA_SHARED_PREF] = name
                params[Config.EMAIL_SHARED_PREF] = email
                params[Config.PASSWORD_PARAM] = password
                return params
            }
        }
        queue.add(stringRequest)

    }

    /**
     * This method is to empty all input edit text
     */
    private fun emptyInputEditText() {
        textInputEditTextName.text = null
        textInputEditTextEmail.text = null
        textInputEditTextPassword.text = null
        textInputEditTextConfirmPassword.text = null
    }
}
