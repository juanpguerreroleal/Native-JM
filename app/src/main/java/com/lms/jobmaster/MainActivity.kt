package com.lms.jobmaster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar!!.hide()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val fragment = FragmentHome()
        loadFragment(fragment)

        bottomNavigationView.selectedItemId = R.id.home
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> {
                    val fragment = FragmentHome()
                    loadFragment(fragment)
                }

                R.id.map -> {
                    val fragment = FragmentMap()
                    loadFragment(fragment)
                }

                R.id.profile -> {

                    val fragment = FragmentProfile()
                    loadFragment(fragment)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

    }

    private fun loadFragment(fragment: Fragment){
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}
