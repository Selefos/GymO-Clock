package com.gym.o.gymoclock

import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.navigation.NavigationView
import com.gym.o.gymoclock.databases.CalendarDB
import com.gym.o.gymoclock.databases.ExercisesScreenDB
import com.gym.o.gymoclock.databases.WorkoutDB
import com.gym.o.gymoclock.databinding.ActivityMainBinding
import com.gym.o.gymoclock.functionality.main_activity_pr.changeNavHeaderText
import com.gym.o.gymoclock.functionality.main_activity_pr.navigation_list_adapter.CustomExpandableListAdapter
import com.gym.o.gymoclock.functionality.main_activity_pr.populateList
import com.gym.o.gymoclock.functionality.main_activity_pr.prepareMenuData
import com.gym.o.gymoclock.functionality.main_activity_pr.setupDrawer
import com.gym.o.gymoclock.functionality.workout_pr.workoutTableName
import com.gym.o.gymoclock.utils.DateTimeUtils
import com.gym.o.gymoclock.utils.DialogBuilderUtils
import com.gym.o.gymoclock.utils.SharedPreferencesUtils
import com.gym.o.gymoclock.utils.TextToSpeechUtils
import io.paperdb.Paper

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

        TextToSpeechUtils.getInstance(this)

        if (SharedPreferencesUtils.getWorkoutTableNameFromPreferences(this).isNotEmpty() && workoutDB.loadWorkoutTableNames().isNotEmpty())
            workoutTableName = SharedPreferencesUtils.getWorkoutTableNameFromPreferences(this)

        calendarDBInit()
        exerciseScreeningInit()
        expandableListView = binding.expandableView
        prepareMenuData()
        populateList()
        drawerLayout = binding.drawerLayout
        setupDrawer()

        val navView: NavigationView = binding.navView
        navView.setNavigationItemSelectedListener(this)
        changeNavHeaderText(SharedPreferencesUtils.getWorkoutTableNameFromPreferences(this))

        Paper.init(this)

    }

    /* init  {
         if (BuildConfig.DEBUG) StrictMode.enableDefaults()
     }*/


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

    private fun exerciseScreeningInit(){
        val exercisesScreeningDB = ExercisesScreenDB(this)
        val screeningMonths: List<String> = exercisesScreeningDB.loadScreeningTableNames()

        DateTimeUtils.getCurrentYear()
        DateTimeUtils.getCurrentMonth()
        DateTimeUtils.setCalendarTableName()
        DateTimeUtils.getDate()
        DateTimeUtils.getCurrentTime()

        for (month in screeningMonths)
            if (DateTimeUtils.setCalendarTableName() == month) {
                Log.d("Screening Database", "Occurrence Found")
                return
            }

        exercisesScreeningDB.addScreeningTable(DateTimeUtils.setCalendarTableName())

    }

    fun changeFragment(fragmentChoice: Class<*>) {
        //Toast.makeText(this, "Pressed", Toast.LENGTH_SHORT).show()
        val lnLayout = findViewById<CoordinatorLayout>(R.id.app_bar_main)
        lnLayout.removeAllViewsInLayout()

        var fragment: Fragment? = null
        var fragmentClass: Class<*>

        fragmentChoice.also { fragmentClass = it }
        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentToRemove = supportFragmentManager.findFragmentById(R.id.nav_workout)
        //supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
//        transaction.remove(fragmentToRemove!!)
//        transaction.replace(R.id.app_bar_main, fragment!!)
//
//        transaction.addToBackStack(null)
//        transaction.commit()
        if (fragmentToRemove != null) {
            Log.i("MainActivity", "Fragment Removed")
            fragmentManager.beginTransaction().remove(fragmentToRemove).commit()
        }
        fragmentManager.beginTransaction().replace(R.id.app_bar_main, fragment!!).commit()

    }

}