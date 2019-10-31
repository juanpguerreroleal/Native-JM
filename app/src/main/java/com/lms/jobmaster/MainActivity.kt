package com.lms.jobmaster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.home -> {
                    Toast.makeText(applicationContext, "This is the home", Toast.LENGTH_SHORT).show()
                }

                R.id.map -> {

                }

                R.id.profile -> {

                }
            }
            return@setOnNavigationItemSelectedListener true
        }

    }
}
