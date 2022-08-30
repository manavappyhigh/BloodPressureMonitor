package com.theappschef.bloodpressuremonitor.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.theappschef.bloodpressuremonitor.R
import com.theappschef.bloodpressuremonitor.fragment.AddBPFragment
import com.theappschef.bloodpressuremonitor.fragment.InfoFragment
import com.theappschef.bloodpressuremonitor.fragment.SettingsFragment
import com.theappschef.bloodpressuremonitor.fragment.TrackerFragment

class MainActivity : AppCompatActivity()  {
    private var mTopFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(TrackerFragment())
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener{
            loadFragment(AddBPFragment())
        }
        findViewById<BottomNavigationView>(R.id.navigation).setOnItemSelectedListener {
            var fragment: Fragment? = null
            when (it.itemId) {
                R.id.navigation_tracker -> {

                    fragment = TrackerFragment()
                }
                R.id.navigation_info -> {

                    fragment = InfoFragment()
                }

                R.id.navigation_settings -> {
                fragment = SettingsFragment()
                }
            }
            loadFragment(fragment)
        }

    }
    private fun loadFragment(fragment: Fragment?): Boolean {
        return try {
            if (fragment != null) {
                mTopFragment = fragment
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
                return true
            }
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}