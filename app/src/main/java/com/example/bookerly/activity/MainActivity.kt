package com.example.bookerly.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.bookerly.fragments.ProfileFragment
import com.example.bookerly.R
import com.example.bookerly.fragments.AboutAppFragment
import com.example.bookerly.fragments.DashboardFragment
import com.example.bookerly.fragments.FavouritesFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var frameLayout: FrameLayout
    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var navigationView: NavigationView

    var previousMenuItem: MenuItem?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        frameLayout = findViewById(R.id.frameLayout)
        toolbar = findViewById(R.id.toolbar)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        navigationView = findViewById(R.id.navigationView)

        setUpToolbar() //calling toolbar function

        openDashboard()

        val actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity,drawerLayout,
            R.string.open_drawer, R.string.close_drawer
        )  //making the toolbar home button functional

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem!=null)
            {
                previousMenuItem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it

            when(it.itemId){
                R.id.dashboard ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, DashboardFragment()).commit()
                        drawerLayout.closeDrawers()
                    supportActionBar?.title="Dashboard"

                }
                R.id.favourites ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FavouritesFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="Favourites"

                }
                R.id.about ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, AboutAppFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="About App"

                }
                R.id.profile ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, ProfileFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="Profile"

                }
            }
            return@setNavigationItemSelectedListener true
        }
    }
    fun setUpToolbar()   //function to add the toolbar to the screen
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title="Toolbar title" //assigning title to the toolbar

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openDashboard()
    {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, DashboardFragment()).commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title="Dashboard"
        navigationView.setCheckedItem(R.id.dashboard) // to highlight the dashboard menu at the start of the app
        //also went to menu file and added group tag in it and valued it to 'single'
    }

    override fun onBackPressed()  //back button functionality
    {
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when(frag)
        {
            !is DashboardFragment -> openDashboard()
            else->  super.onBackPressed()
        }

    }
}