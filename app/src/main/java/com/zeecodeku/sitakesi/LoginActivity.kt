package com.zeecodeku.sitakesi

import android.content.Context
import android.content.Intent
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

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val activity = this@LoginActivity

    private lateinit var nestedScrollView: NestedScrollView

    private lateinit var textInputLayoutEmail: TextInputLayout
    private lateinit var textInputLayoutPassword: TextInputLayout

    private lateinit var textInputEditTextEmail: TextInputEditText
    private lateinit var textInputEditTextPassword: TextInputEditText

    private lateinit var appCompatButtonLogin: AppCompatButton

    private lateinit var textViewLinkRegister: AppCompatTextView
    private lateinit var textViewForgotPassword: AppCompatTextView

    private lateinit var inputValidation: InputValidation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

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
        textInputLayoutPassword = findViewById<View>(R.id.textInputLayoutPassword) as TextInputLayout

        textInputEditTextEmail = findViewById<View>(R.id.textInputEditTextEmail) as TextInputEditText
        textInputEditTextPassword = findViewById<View>(R.id.textInputEditTextPassword) as TextInputEditText

        appCompatButtonLogin = findViewById<View>(R.id.appCompatButtonLogin) as AppCompatButton

        textViewLinkRegister = findViewById<View>(R.id.textViewLinkRegister) as AppCompatTextView
        textViewForgotPassword = findViewById<View>(R.id.textViewForgotPassword) as AppCompatTextView

    }

    /**
     * This method is to initialize listeners
     */
    private fun initListeners() {

        appCompatButtonLogin.setOnClickListener(this)
        textViewLinkRegister.setOnClickListener(this)
        textViewForgotPassword.setOnClickListener(this)
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
            R.id.appCompatButtonLogin -> verifyFromLogin()
            R.id.textViewLinkRegister -> {
                // Navigate to RegisterActivity
                val intentRegister = Intent(applicationContext, SignupActivity::class.java)
                startActivity(intentRegister)
            }
            R.id.textViewForgotPassword -> {
                val intentForgot = Intent(applicationContext, ForgotActivity::class.java)
                startActivity(intentForgot)
            }
        }
    }

    /**
     * This method is to validate the input text fields and verify login credentials
     */
    private fun verifyFromLogin() {

        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_email))) {
            return
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return
        }

        AppUtils.progressDialog(activity, activity.getString(R.string.auth))
        val email = textInputEditTextEmail.text.toString().trim()
        val password = textInputEditTextPassword.text.toString().trim()

        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, Config.LOGIN_URL,
            Response.Listener { response ->
                when {
                    response.contains(Config.SUCCESS) -> {
                        val sharedPreferences =
                            activity.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        val data = response.split("~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true)
                        editor.putString(Config.ID_ANGGOTA_SHARED_PREF, data[1])
                        editor.putString(Config.NAMA_SHARED_PREF, data[2])
                        editor.putString(Config.EMAIL_SHARED_PREF, email)
                        editor.putString(Config.DATE_ANGGOTA_SHARED_PREF, data[3])
                        editor.apply()
                        emptyInputEditText()
                        AppUtils.hideProgressDialog()
                        val intent = Intent(activity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    response.contains(Config.NOT_VERIFIED) -> {
                        AppUtils.hideProgressDialog()
                        Snackbar.make(nestedScrollView, getString(R.string.error_not_verified), Snackbar.LENGTH_LONG).show()
                    }
                    response.contains(Config.WRONG_EMAIL) -> {
                        AppUtils.hideProgressDialog()
                        Snackbar.make(nestedScrollView, getString(R.string.error_valid_email), Snackbar.LENGTH_LONG).show()
                    }
                    response.contains(Config.WRONG_PASSWORD) -> {
                        AppUtils.hideProgressDialog()
                        Snackbar.make(nestedScrollView, getString(R.string.error_valid_password), Snackbar.LENGTH_LONG).show()
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
        textInputEditTextEmail.text = null
        textInputEditTextPassword.text = null
    }
}
