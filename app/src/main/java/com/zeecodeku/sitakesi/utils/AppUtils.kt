package com.zeecodeku.sitakesi.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.zeecodeku.sitakesi.R

object AppUtils {

    fun hasUserLoggedin(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false)
    }

    fun getIdAnggota(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Config.ID_ANGGOTA_SHARED_PREF, "")
    }

    fun getName(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Config.NAMA_SHARED_PREF, "")
    }

    fun getOrganisasi(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Config.GROUPJSON_SHARED_PREF, "")
    }

    fun getTagihan(args: String, context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Config.TAGIHANJSON_SHARED_PREF+args, "")
    }

    fun isNetworkStatusAvialable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                @Suppress("DEPRECATION")
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

    fun hasUserSeenOnboarding(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.pref_has_user_seen_onboarding), false)
    }

    fun deletePref(context: Context){
        val preferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog

    fun progressDialog(context: Context, message: String){
        builder = AlertDialog.Builder(context)
        val dialogLayout = View.inflate(context, R.layout.dialog_layout, null)
        val textView = dialogLayout.findViewById<TextView>(R.id.textView)
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        textView.text = message
        dialog = builder.create()
        dialog.show()
    }

    fun hideProgressDialog() {
        dialog.dismiss()
    }

}