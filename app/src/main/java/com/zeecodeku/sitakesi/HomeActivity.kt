package com.zeecodeku.sitakesi

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.text.HtmlCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.zeecodeku.sitakesi.adapters.OrganisasiAdapter
import com.zeecodeku.sitakesi.helpers.InputValidation
import com.zeecodeku.sitakesi.models.OrganisasiModel
import com.zeecodeku.sitakesi.parsing.OrganisasiJSONParser
import com.zeecodeku.sitakesi.utils.AppUtils
import com.zeecodeku.sitakesi.utils.Config
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class HomeActivity : AppCompatActivity(), View.OnClickListener, OrganisasiAdapter.RecyclerViewItemClickListener {

    private val activity = this@HomeActivity

    private var isFabOpen = false

    private lateinit var toolbar: Toolbar
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var organisasiAdapter: OrganisasiAdapter

    private lateinit var fab: FloatingActionButton
    private lateinit var fab1: FloatingActionButton
    private lateinit var fab2: FloatingActionButton
    private lateinit var textFab1: AppCompatTextView
    private lateinit var textFab2: AppCompatTextView

    private lateinit var animFabOpen: Animation
    private lateinit var animFabClose: Animation
    private lateinit var animRotateForward: Animation
    private lateinit var animRotateBackward: Animation

    private lateinit var inputValidation: InputValidation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    val await = Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 3000.toLong())
                    return if (await && AppUtils.hasUserLoggedin(activity)) {
                        // The content is ready; start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        if (!AppUtils.hasUserLoggedin(activity)) {
                            startActivity(Intent(activity, LoginActivity::class.java))
                            finish()
                        }

                        if (!AppUtils.hasUserSeenOnboarding(activity)) {
                            startActivity(Intent(activity, OnboardingActivity::class.java))
                            finish()
                        }
                        false
                    }
                }
            }
        )

        // initializing the views
        initViews()

        organisasiAdapter = OrganisasiAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = organisasiAdapter

        // initializing the listeners
        initListeners()

        // initializing the objects
        initObjects()

        toolbar.title = getString(R.string.hello)+" "+AppUtils.getName(activity)
        setSupportActionBar(toolbar)

        if (AppUtils.getOrganisasi(activity) != "") setRecyclerView(AppUtils.getOrganisasi(activity)!!)
        if (AppUtils.isNetworkStatusAvialable(activity)) getDataOnline()
    }

    private fun getDataOnline() {
        progressBar.visibility = View.VISIBLE
        val idAnggota = AppUtils.getIdAnggota(activity)!!
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, Config.CHECK_ORGANIZATION_URL,
            Response.Listener { response ->
                if (response.contains(Config.FAILURE)){
                    Snackbar.make(coordinatorLayout, getString(R.string.message_no_organization), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.ok)) {
                            animateFAB()
                        }.show()
                } else {
                    setRecyclerView(response)
                    val sharedPreferences =
                        activity.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString(Config.GROUPJSON_SHARED_PREF, response)
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
                params[Config.ID_ANGGOTA_SHARED_PREF] = idAnggota
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun setRecyclerView(response: String){
        val groupList: List<OrganisasiModel> = OrganisasiJSONParser.parseData(response)
        organisasiAdapter.updateWith(groupList)
    }

    override fun clickOnItem(data: OrganisasiModel) {
        val intentOrganization = Intent(activity, OrganizationActivity::class.java)
        intentOrganization.putExtra(Config.EXTRA_ORGANIZATION, data)
        if (data.getStatusOrganisasi()=="aktif")
            startActivity(intentOrganization)
        else Snackbar.make(coordinatorLayout, getString(R.string.message_payment), Snackbar.LENGTH_SHORT)
            .setAction(getString(R.string.how)){
                popUpActive(data)
            }.show()
    }

    /**
     * This method is to initialize views
     */
    private fun initViews() {
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        coordinatorLayout = findViewById<View>(R.id.coordinator_layout) as CoordinatorLayout
        fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab1 = findViewById<View>(R.id.fab1) as FloatingActionButton
        fab2 = findViewById<View>(R.id.fab2) as FloatingActionButton
        textFab1 = findViewById<View>(R.id.textFab1) as AppCompatTextView
        textFab2 = findViewById<View>(R.id.textFab2) as AppCompatTextView
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
    }

    /**
     * This method is to initialize listeners
     */
    private fun initListeners() {
        fab.setOnClickListener(this)
        fab1.setOnClickListener(this)
        fab2.setOnClickListener(this)
    }

    /**
     * This method is to initialize objects to be used
     */
    private fun initObjects() {
        animFabOpen = AnimationUtils.loadAnimation(activity, R.anim.fab_open)
        animFabClose = AnimationUtils.loadAnimation(activity, R.anim.fab_close)
        animRotateForward = AnimationUtils.loadAnimation(activity, R.anim.rotate_forward)
        animRotateBackward = AnimationUtils.loadAnimation(activity, R.anim.rotate_backward)
        inputValidation = InputValidation(activity)
    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> animateFAB()
            R.id.fab1 -> {
                val intentCreate = Intent(applicationContext, CreateOrganisasiActivity::class.java)
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    getDataOnline()
                }.launch(intentCreate)
            }
            R.id.fab2 -> popUpJoin()
        }
    }

    private fun popUpActive(data: OrganisasiModel) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.how_to_active))
        builder.setMessage(HtmlCompat.fromHtml(getString(R.string.active_group), HtmlCompat.FROM_HTML_MODE_LEGACY))
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setNeutralButton(getString(R.string.confirm_payment)) { dialog, _ ->
            dialog.dismiss()
            val contact = "+6289625840513" // use country code with your phone number
            val message = getString(R.string.active_message)+" "+"*${data.getReferral()}*"
            val url = "https://wa.me/$contact/?text=$message"
            try {
                packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(activity, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT)
                    .show()
                e.printStackTrace()
            }
        }
        builder.show()
    }

    private fun popUpJoin() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.join_organization)
        val dialogLayout = View.inflate(activity, R.layout.search_layout, null)
        val editText = dialogLayout.findViewById<TextInputEditText>(R.id.textInputEditTextOrganization)
        val inputEditText = dialogLayout.findViewById<TextInputLayout>(R.id.textInputLayoutOrganization)
        builder.setView(dialogLayout)
        builder.setPositiveButton(R.string.ok) { _, _ -> }
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (inputValidation.isInputEditTextFilled(editText, inputEditText, getString(R.string.error_message_referral))) {
                dialog.dismiss()
                AppUtils.progressDialog(activity, activity.getString(R.string.searching_organization))
                val referral = editText.text.toString().trim()
                val idAnggota = AppUtils.getIdAnggota(activity)!!
                val queue = Volley.newRequestQueue(this)
                val stringRequest = object : StringRequest(Method.POST, Config.SEARCH_ORGANIZATION_URL,
                    Response.Listener { response ->
                        if (response.contains(Config.SUCCESS)) {
                            getDataOnline()
                            Snackbar.make(coordinatorLayout, getString(R.string.success_join_message), Snackbar.LENGTH_SHORT).show()
                        } else if (response.contains(Config.FAILURE)){
                            Snackbar.make(coordinatorLayout, getString(R.string.already_join_message), Snackbar.LENGTH_SHORT).show()
                        }
                        AppUtils.hideProgressDialog()
                    },
                    Response.ErrorListener {
                        AppUtils.hideProgressDialog()
                        Toast.makeText(activity, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
                    }){
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params[Config.REFERRAL_PARAM] = referral
                        params[Config.ID_ANGGOTA_SHARED_PREF] = idAnggota
                        return params
                    }
                }
                queue.add(stringRequest)
            }
        }
    }

    private fun animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(animRotateBackward)
            fab1.startAnimation(animFabClose)
            fab2.startAnimation(animFabClose)
            textFab1.startAnimation(animFabClose)
            textFab2.startAnimation(animFabClose)
            fab1.isClickable = false
            fab2.isClickable = false
            isFabOpen = false
            Log.d("fab", "close")

        } else {

            fab.startAnimation(animRotateForward)
            fab1.startAnimation(animFabOpen)
            fab2.startAnimation(animFabOpen)
            textFab1.startAnimation(animFabOpen)
            textFab2.startAnimation(animFabOpen)
            fab1.isClickable = true
            fab2.isClickable = true
            isFabOpen = true
            Log.d("fab", "open")

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.setting -> {
                val intent = Intent(activity, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        if (!AppUtils.hasUserLoggedin(this)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        super.onResume()
    }

}
