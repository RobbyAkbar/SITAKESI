package com.zeecodeku.sitakesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.zeecodeku.sitakesi.fragments.SettingFragment

class SettingActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SettingFragment()).commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
