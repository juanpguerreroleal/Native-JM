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
        loadFragment(fragment,"homeFragment")

        floatinActionMenu = findViewById(R.id.menu_fab)

        addJobButton = findViewById(R.id.addWork)

        addJobButton.setOnClickListener{

            var fragment = fragmentManager.findFragmentByTag("mapFragment") as FragmentMap

            fragment.postDataWork()

            floatinActionMenu.collapse()

        }

        bottomNavigationView.selectedItemId = R.id.home
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> {
                    val fragment = FragmentHome()
                    loadFragment(fragment,"homeFragment")
                }

                R.id.map -> {
                    val fragment = FragmentMap()
                    loadFragment(fragment,"mapFragment")
                }

                R.id.profile -> {

                    val fragment = FragmentProfile()
                    loadFragment(fragment,"profileFragment")
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

    }

    private fun loadFragment(fragment: Fragment, tag : String){
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_holder, fragment,tag)
        transaction.addToBackStack(null)
        transaction.commit()

    }


}
