package com.lms.jobmaster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.getbase.floatingactionbutton.FloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.add_job_dialog.view.*

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

        val fragmentHome = FragmentHome()
        loadFragment(fragmentHome,"homeFragment")

        floatinActionMenu = findViewById(R.id.menu_fab)

        addJobButton = findViewById(R.id.addWork)

        addJobButton.setOnClickListener{


            floatinActionMenu.collapse()


            val dialogAddJob = LayoutInflater.from(this).inflate(R.layout.add_job_dialog,null)

            val dialogAddJobBuilder = AlertDialog.Builder(this).setView(dialogAddJob).setTitle("Add a new job")

            val alertDialog = dialogAddJobBuilder.show()

            dialogAddJob.button_save_job.setOnClickListener {


                val title = dialogAddJob.input_job_title.text.toString()
                val expiration = dialogAddJob.input_job_expiration.text.toString()
                val description = dialogAddJob.input_job_description.text.toString()

                val fragment = fragmentManager.findFragmentByTag("mapFragment") as FragmentMap

                fragment.postDataWork(title,expiration,description)

                alertDialog.dismiss()

            }

            dialogAddJob.button_cancel_job.setOnClickListener {
                alertDialog.dismiss()
            }

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
