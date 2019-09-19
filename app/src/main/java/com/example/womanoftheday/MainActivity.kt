package com.example.womanoftheday

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import java.text.SimpleDateFormat
import kotlin.random.Random
import android.util.Log
import android.widget.TextView




class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        val myDbHelper = DataBaseHelper(this)
        myDbHelper.createDataBase()
        myDbHelper.openDataBase()
        Log.d("DatB", myDbHelper.getUserNameFromDB())
//        val userName = myDbHelper.getUserNameFromDB();
//        var book1984 = Book("1984", "George Orwell")
//        myDbHelper.addBook(book1984)
//        myDbHelper.getAllBooks()
//
        setDailyWoman(myDbHelper)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_share -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun setDailyWoman(myDbHelper : DataBaseHelper)  {
        // Will select a woman of the day from the database and set it in the app
        val currDate = SimpleDateFormat().format(0)
        Log.d("Date", "Hello World")
        Log.d("Date", currDate)


        val rand = Random(0)
        // Get the current date and use this to get a random number seed

        // Get the database info

//        Log.d("db", result)
//

        // Get a value from the database
        // Set the woman details in the 3 text views and the picture periodically (daily).
        //  ( https://stackoverflow.com/questions/4459058/alarm-manager-example )
        val titleTextView: TextView = findViewById(R.id.titleTextView)
//        titleTextView.text = myDbHelper.getBook(1).author;
    }
}
