package com.example.womanoftheday

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import java.text.SimpleDateFormat
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import java.text.Normalizer
import java.util.*


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
        setDailyWoman(myDbHelper)
        myDbHelper.close();
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

    // Will get an index corresponding to the date
    fun getRandNum(lowerBound: Long, upperBound: Long): Long {
        val currDate: Date = Calendar.getInstance().time
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val origin: Date = sdf.parse("30/10/2019")!!
        val diffDate = (currDate.getTime() - origin.getTime())/(60 * 1000 * 60 * 24)

        val betweenBounds = upperBound - lowerBound
        val returnNum = ((diffDate % betweenBounds) + lowerBound)
        Log.d("Date", returnNum.toString())

        return returnNum
    }

    fun String.normalize(): String {
        val original = arrayOf("Ā", "ā", "Ă", "ă", "Ą", "ą", "Ć", "ć", "Ĉ", "ĉ", "Ċ",
                                            "ċ", "Č", "č", "Ď", "ď", "Đ", "đ", "Ē", "ē", "Ĕ", "ĕ",
                                            "Ė", "ė", "Ę", "ę", "Ě", "ě", "Ĝ", "ĝ", "Ğ", "ğ", "Ġ",
                                            "ġ", "Ģ", "ģ", "Ĥ", "ĥ", "Ħ", "ħ", "Ĩ", "ĩ", "Ī", "ī",
                                            "Ĭ", "ĭ", "Į", "į", "İ", "ı", "Ĳ", "ĳ", "Ĵ", "ĵ", "Ķ",
                                            "ķ", "ĸ", "Ĺ", "ĺ", "Ļ", "ļ", "Ľ", "ľ", "Ŀ", "ŀ", "Ł",
                                            "ł", "Ń", "ń", "Ņ", "ņ", "Ň", "ň", "ŉ", "Ŋ", "ŋ", "Ō",
                                            "ō", "Ŏ", "ŏ", "Ő", "ő", "Œ", "œ", "Ŕ", "ŕ", "Ŗ", "ŗ",
                                            "Ř", "ř", "Ś", "ś", "Ŝ", "ŝ", "Ş", "ş", "Š", "š", "Ţ",
                                            "ţ", "Ť", "ť", "Ŧ", "ŧ", "Ũ", "ũ", "Ū", "ū", "Ŭ", "ŭ",
                                            "Ů", "ů", "Ű", "ű", "Ų", "ų", "Ŵ", "ŵ", "Ŷ", "ŷ", "Ÿ",
                                            "Ź", "ź", "Ż", "ż", "Ž", "ž", "ſ")
//        val normalized =  arrayOf("e", "s")

        return this.map {
            val index = original.indexOf(it.toString())
            if (index >= 0) ""
            else it.toString()
        }.joinToString("")
    }

    fun setDailyWoman(myDbHelper : DataBaseHelper)  {
        // Will select a woman of the day from the database and set it in the app
        var randInt: Int = getRandNum(1, 1500).toInt()

        // Get a value from the database
        // Set the woman details in the 3 text views and the picture periodically (daily).
        //  ( https://stackoverflow.com/questions/4459058/alarm-manager-example )
        //var randInt: Int = (0..100).random()
        Log.d("DatB", "Random Integer = " + randInt.toString())

        // Set the title
        val titleTextView: TextView = findViewById(R.id.titleTextView)
        val name: String = myDbHelper.getName(randInt)
        setTextView(titleTextView, name)

        // Set the info box
        val summTextView: TextView = findViewById(R.id.quickSummTextView)
        Log.d("DatB", myDbHelper.getName(randInt))
        setTextView(summTextView, myDbHelper.getSumm(randInt))

        // Set the biography
        val biogTextView: TextView = findViewById(R.id.longBioTextView)
        setTextView(biogTextView, myDbHelper.getBio(randInt))

        // Set the image
        val img: ImageView = findViewById(R.id.womanPic)
        var mDrawableName: String = name.trim().replace(" ", "_")
        mDrawableName = mDrawableName.toLowerCase()
        mDrawableName = mDrawableName.replace("[^a-z0-9_\\s:]", "")
        mDrawableName = mDrawableName.replace("é", "").normalize()
        Log.d("DatB", mDrawableName)
        val res = resources
        val resID = res.getIdentifier(mDrawableName, "drawable", packageName)
        img.setImageResource(resID)


        Log.d("DatB", "Set the picture")
        Log.d("DatB", myDbHelper.getBio(randInt))
    }
}

fun setTextView(textView: TextView, text: String){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        textView.setText(
            HtmlCompat.fromHtml(text,
                HtmlCompat.FROM_HTML_MODE_LEGACY));
    } else {
        @Suppress("DEPRECATION")
        textView.setText(Html.fromHtml(text));
    }
}
