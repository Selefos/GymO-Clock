package com.gym.o.gymoclock

import android.content.res.Configuration
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.gym.o.gymoclock.databases.CalendarDB
import com.gym.o.gymoclock.databases.ExercisesScreenDB
import com.gym.o.gymoclock.databases.WorkoutDB
import com.gym.o.gymoclock.databinding.ActivityMainBinding
import com.gym.o.gymoclock.functionality.main_activity_pr.changeNavHeaderText
import com.gym.o.gymoclock.functionality.main_activity_pr.populateList
import com.gym.o.gymoclock.functionality.main_activity_pr.prepareMenuData
import com.gym.o.gymoclock.functionality.main_activity_pr.setupDrawer
import com.gym.o.gymoclock.functionality.workout_pr.navigation_list_adapter.CustomExpandableListAdapter
import com.gym.o.gymoclock.functionality.workout_pr.workoutTableName
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import com.gym.o.gymoclock.utils.DateTimeUtils
import com.gym.o.gymoclock.utils.SharedPreferencesUtils

import com.gym.o.gymoclock.utils.TextToSpeechUtils

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding

    lateinit var drawerLayout: DrawerLayout
    lateinit var drawerToggle: ActionBarDrawerToggle

    lateinit var expandableListView: ExpandableListView
    lateinit var expandableAdapter: CustomExpandableListAdapter

    var workoutDB: WorkoutDB = WorkoutDB(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.appBarMain.toolbar)

        TextToSpeechUtils.getInstance(this)

        //sharedPreferences = this.getSharedPreferences("WorkoutTableName", Context.MODE_PRIVATE)

        if (SharedPreferencesUtils.getWorkoutTableNameFromPreferences(this).isNotEmpty() && workoutDB.loadWorkoutTableNames().isNotEmpty())
            workoutTableName = SharedPreferencesUtils.getWorkoutTableNameFromPreferences(this)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        calendarDBInit()


        expandableListView = binding.expandableView
        prepareMenuData()
        populateList()
        drawerLayout = binding.drawerLayout
        setupDrawer()

//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
//        val navController = navHostFragment.navController
        //val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.nav_workout, R.id.nav_calendar, R.id.nav_slideshow
//            ), drawerLayout
//        )
        //setupActionBarWithNavController(navController, appBarConfiguration)

        val navView: NavigationView = binding.navView
        navView.setNavigationItemSelectedListener(this)
        //navView.setupWithNavController(navController) //@id/nav_view

        //val headerView: View = layoutInflater.inflate(R.layout.nav_header_main,null, false)
        // expandableListView.addHeaderView(headerView)

        changeNavHeaderText(SharedPreferencesUtils.getWorkoutTableNameFromPreferences(this))

//        binding.root.setOnTouchListener {
//                _, event ->
//            val x = event.x
//            val y = event.y
//            binding.appBarMain.fab.x = x
//            binding.appBarMain.fab.y = y
////            editTextX.setText(x.toString())
////            editTextY.setText(y.toString())
//            true
//        }

//        binding.appBarMain.fab.setOnLongClickListener{
//            View ->
//            Toast.makeText(this, "Longed", Toast.LENGTH_SHORT).show()
//            binding.root.setOnTouchListener {
//                    _, event ->
//                val x = event.x
//                val y = event.y
//                binding.appBarMain.fab.x = x
//                binding.appBarMain.fab.y = y
//                Toast.makeText(this, "Toasted", Toast.LENGTH_SHORT).show()
////            editTextX.setText(x.toString())
////            editTextY.setText(y.toString())
//                true
//            }
//            true
//        }
//        val calendarDB = CalendarDB(this)
//        val sqlDB: SQLiteDatabase = calendarDB.readableDatabase
//        val monthYear = "${DateTimeUtils.getCurrentMonth()} ${DateTimeUtils.getCurrentYear()}".replace(" ", "_")
//        val cursor: Cursor = calendarDB.getCalendarWorkoutID(monthYear, "date", sqlDB)
//
//        val exercisesScreenDB: ExercisesScreenDB = ExercisesScreenDB(this)
//        exercisesScreenDB.insertScreenDBDetails(1, workoutTableName, DateTimeUtils.getDate(), "test,asdd,asd,fdsfda,adasd")
    }

    /* init  {
         if (BuildConfig.DEBUG) StrictMode.enableDefaults()
     }*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //val id: Int = item.itemId
//        if (id == R.id.nav_workout)
//            return true
        if (drawerToggle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(Configuration())
    }

/*    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }*/


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        // Handle navigation view item clicks here.

        when (item.itemId) {
            1 -> Toast.makeText(this, "Pressed", Toast.LENGTH_SHORT).show()

        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onDestroy() {
        //TextToSpeechUtils.getInstance(this).stopTTS()
        Log.i("Main", "onDestroy")
        super.onDestroy()
    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        TextToSpeechUtils.getInstance(this)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        TextToSpeechUtils.getInstance(this)
    }


    private fun calendarDBInit() {
        val calendarDB = CalendarDB(this)
        val calendarMonths: List<String> = calendarDB.loadCalendarTableNames()
        //val dateTimeUtils = DateTimeUtils()

        DateTimeUtils.getCurrentYear()
        DateTimeUtils.getCurrentMonth()
        DateTimeUtils.setCalendarTableName()
        DateTimeUtils.getDate()
        DateTimeUtils.getCurrentTime()

        for (month in calendarMonths)
            if (DateTimeUtils.setCalendarTableName() == month) {
                Log.d("Calendar Database", "Occurrence Found")
                return
            }

        calendarDB.addCalendarTable(DateTimeUtils.setCalendarTableName())

    }


    fun changeFragment(fragmentChoice: Class<*>) {
        //Toast.makeText(this, "Pressed", Toast.LENGTH_SHORT).show()
        var fragment: Fragment? = null
        var fragmentClass: Class<*>
        fragmentChoice.also { fragmentClass = it }
        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val fragmentManager: FragmentManager = supportFragmentManager
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(WorkoutFragment()).commit()
        }
        fragmentManager.beginTransaction().replace(R.id.app_bar_main, fragment!!).commit()
    }

}