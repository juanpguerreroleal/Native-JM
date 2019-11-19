package com.lms.jobmaster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.getbase.floatingactionbutton.FloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val fragmentManager = supportFragmentManager

    private lateinit var addJobButton : FloatingActionButton
    private lateinit var floatinActionMenu : FloatingActionsMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar!!.hide()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val fragment = FragmentHome()
        loadFragment(fragment)

        floatinActionMenu = findViewById(R.id.menu_fab)

        addJobButton = findViewById(R.id.addWork)

        addJobButton.setOnClickListener{
            FragmentMap().postDataWork()
            floatinActionMenu.collapse()

        }

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
