package com.zeecodeku.sitakesi.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.zeecodeku.sitakesi.R
import com.zeecodeku.sitakesi.adapters.AgreementAdapter
import com.zeecodeku.sitakesi.parsing.AgreementJSONParser
import com.zeecodeku.sitakesi.utils.AppUtils
import com.zeecodeku.sitakesi.utils.Config

/**
 * A simple [Fragment] subclass.
 *
 */
class SettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.app_preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.account) -> logout()
            getString(R.string.help) -> {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(Config.VIDEO_URL)
                startActivity(openURL)
            }
            getString(R.string.about) -> about()
            getString(R.string.tos) -> agreement()
            getString(R.string.feedback) -> feedback()
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun agreement() {
        val builder = AlertDialog.Builder(requireContext())
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
        }

        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(
            Request.Method.GET, Config.AGREEMENT_URL,
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

    private fun feedback() {
        val contact = "+6283816707090" // use country code with your phone number
        val message = getString(R.string.message_feedback)
        val url = "https://wa.me/$contact/?text=$message"
        try {
            requireActivity().packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(activity, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    }

    private fun logout() {
        val builder = AlertDialog.Builder(requireContext(), R.style.AppCompatAlertDialogStyle)
        builder.setMessage(getString(R.string.message_logout))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                AppUtils.deletePref(requireContext())
                requireActivity().finish()
            }
            .setNegativeButton(
                getString(R.string.no)
            ) { dialogInterface, _ -> dialogInterface.cancel() }
            .show()
    }

    private fun about() {
        val builder = AlertDialog.Builder(requireContext(), R.style.AppCompatAlertDialogStyle)
        val dialogLayout = View.inflate(activity, R.layout.about_layout, null)
        builder.setCancelable(true)
            .setView(dialogLayout)
            .setPositiveButton(getString(R.string.ok)) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .show()
    }

}
