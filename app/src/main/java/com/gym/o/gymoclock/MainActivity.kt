package com.gym.o.gymoclock

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemLongClickListener
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
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
import com.gym.o.gymoclock.databases.WorkoutDB
import com.gym.o.gymoclock.databinding.ActivityMainBinding
import com.gym.o.gymoclock.functionality.workout_pr.edit_workout.WidgetsWarnings
import com.gym.o.gymoclock.functionality.workout_pr.workoutName
import com.gym.o.gymoclock.navigation_list_adapter.CustomExpandableListAdapter
import com.gym.o.gymoclock.ui.calendar.CalendarFragment
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import com.gym.o.gymoclock.utils.DateTimeUtils
import com.gym.o.gymoclock.utils.TextToSpeechUtils
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var expandableListView: ExpandableListView
    private lateinit var expandableAdapter: CustomExpandableListAdapter

    private var workoutDB: WorkoutDB = WorkoutDB(this)
    private var listTitle: MutableList<String> = ArrayList()
    private var listChild = HashMap<String, List<String>?>()

    private lateinit var dialogBuilder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.appBarMain.toolbar)

        TextToSpeechUtils.getInstance(this)

        sharedPreferences = this.getSharedPreferences("WorkoutTableName", Context.MODE_PRIVATE)
        if (sharedPreferences.getString("workoutName", "").toString()
                .isNotEmpty() && workoutDB.loadWorkoutTableNames().isNotEmpty()
        )
            workoutName = sharedPreferences.getString("workoutName", "").toString()


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
        val id: Int = item.itemId
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

    private fun setupDrawer() {
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.closed)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

    }

    private fun prepareMenuData() {

        listTitle.clear()
        workoutDB = WorkoutDB(this)
        val workoutNames: List<String> = workoutDB.loadWorkoutTableNames()
        var childModelsList: MutableList<String> = ArrayList()
        var childModel: String//MenuModel

        var menuModel = "Workout"//MenuModel("Workout") //Menu of Android Tutorial. No sub menus
        listTitle.add(menuModel)
        for (i in workoutNames) {
            childModel = i.replace("_", " ")
            childModelsList.add(childModel)
        }

        //if (menuModel.hasChildren) {
        listChild[menuModel] = childModelsList
        //}
        //childModelsList = ArrayList()
        menuModel = "Calendar"//MenuModel("Calendar")
        listTitle.add(menuModel)

    }

    private fun populateList() {
        expandableAdapter = CustomExpandableListAdapter(this, listTitle, listChild)
        expandableListView.setAdapter(expandableAdapter)

        expandableListView.setOnGroupClickListener { parent, v, groupPosition, id ->
            when (id) {
                1L -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    changeFragment(CalendarFragment::class.java)
                }
            }
            //Toast.makeText(this, "Pressed", Toast.LENGTH_SHORT).show()
            false
        }

        expandableListView.setOnGroupExpandListener {
            fun onGroupExpand(groupPosition: Int) {
                supportActionBar?.title = listTitle[groupPosition].toString()
            }
        }

        expandableListView.setOnGroupCollapseListener {
            fun onGroupCollapse(groupPosition: Int) {
                supportActionBar?.title = "GymO'Clock"
            }
        }

        expandableListView.setOnChildClickListener(ExpandableListView.OnChildClickListener(fun(
            parent: ExpandableListView,
            v: View,
            groupPosition: Int,
            childPosition: Int,
            id: Long
        ): Boolean {

            val selectedItem = listChild[listTitle[groupPosition]]?.get(childPosition).toString()
            supportActionBar?.title = childPosition.toString()

            supportActionBar?.title = selectedItem
            val handler = Handler()
            changeNavHeaderText(selectedItem)
            handler.postDelayed(Runnable { drawerLayout.closeDrawer(GravityCompat.START) }, 500)
            //drawerLayout.closeDrawer(GravityCompat.START)
            when (selectedItem) {
                selectedItem -> {
                    workoutName = selectedItem.replace(" ", "_")
                    saveWorkoutNameToPreferences(workoutName)

                    changeFragment(WorkoutFragment::class.java)
                }
            }
            return true
        }))

        expandableListView.onItemLongClickListener =
            OnItemLongClickListener { parent, view, position, id ->
                if (ExpandableListView.getPackedPositionType(
                        id
                    ) == ExpandableListView.PACKED_POSITION_TYPE_GROUP
                ) {
                    // You now have everything that you would as if this was an OnChildClickListener()
                    // Add your logic here.
                    // Return true as we are handling the event.
                    when (id) {
                        0L -> addWorkoutTable()
                    }
                    return@OnItemLongClickListener true
                }

                if (ExpandableListView.getPackedPositionType(
                        id
                    ) == ExpandableListView.PACKED_POSITION_TYPE_CHILD
                ) {
                    val groupPosition = ExpandableListView.getPackedPositionGroup(id)
                    val childPosition = ExpandableListView.getPackedPositionChild(id)
                    val selectedItem =
                        listChild[listTitle[groupPosition]]?.get(childPosition).toString()

                    editWorkoutName(selectedItem)
                    return@OnItemLongClickListener true
                }

                false
            }
    }

    private fun saveWorkoutNameToPreferences(workoutNameString: String) {
        val save = sharedPreferences.edit()
        save.putString("workoutName", workoutNameString)
        save.apply()

    }

    private fun changeNavHeaderText(selectedItem: String) {

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        val navUsername = headerView.findViewById(R.id.exercise_name_navigation_view) as TextView
        navUsername.text = selectedItem

    }

    private fun changeFragment(fragmentChoice: Class<*>) {
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

    private fun addWorkoutTable() {
        dialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val inflater = layoutInflater//LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_add_new_workout, null)
        val workoutNameAdd = view.findViewById<EditText>(R.id.workout_name)
        val addButton = view.findViewById<Button>(R.id.add_workout_button)
        val cancelButton = view.findViewById<Button>(R.id.cancel_add_workout_button)

        dialogBuilder.setView(view)
        dialog = dialogBuilder.create()
        dialog.show()

        addButton.setOnClickListener {
            if (isTextEmpty(workoutNameAdd).isNotEmpty() && isNotDuplicate(workoutNameAdd, workoutDB).isNotEmpty() && isNotTextNumberOrSymbol(workoutNameAdd).isNotEmpty()) {
                workoutDB.addWorkoutTable(workoutNameAdd.text.toString())
                //isNotTextNumberOrSymbol(workoutNameAdd).let { add -> workoutDB.addWorkoutTable(add) }
                prepareMenuData()
                expandableAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
        }

        cancelButton.setOnClickListener { dialog.dismiss() }

    }

    private fun editWorkoutName(workoutName: String) {
        dialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val inflater = layoutInflater//LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_delete_rename_workout, null)
        val editIndicator = view.findViewById<TextView>(R.id.edit_indicator)
        val renameWorkout = view.findViewById<EditText>(R.id.rename_text_workout)
        val updateWorkoutButton = view.findViewById<Button>(R.id.rename_workout)
        val deleteWorkoutButton = view.findViewById<Button>(R.id.delete_workout)

        val updateTextView: String = getString(R.string.edit_indicator) + " " + workoutName
        editIndicator.text = updateTextView

        val updateEditTextHint: String = getString(R.string.rename) + " \"" + workoutName + "\""
        renameWorkout.hint = updateEditTextHint

        dialogBuilder.setView(view)
        dialog = dialogBuilder.create()
        dialog.show()

        updateWorkoutButton.setOnClickListener {
            if (isTextEmpty(renameWorkout).isNotEmpty() && isNotDuplicate(renameWorkout, workoutDB).isNotEmpty() && isNotTextNumberOrSymbol(renameWorkout).isNotEmpty()) {
                workoutDB.renameWorkoutTable(workoutName, renameWorkout.text.toString().replace(" ", "_"))
                //isNotTextNumberOrSymbol(renameWorkout).let { rnm -> workoutDB.renameWorkoutTable(workoutName.replace(" ", "_"), rnm) }
                //workoutDB.renameTable(workoutName.replace(" ", "_"), prepareWorkoutValue(renameWorkout)) // alternative update

                prepareMenuData()
                expandableAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
        }

        deleteWorkoutButton.setOnClickListener {
            workoutDB.deleteWorkoutTable(workoutName.replace(" ", "_"))
            prepareMenuData()
            expandableAdapter.notifyDataSetChanged()
            //Toast.makeText(this, "Deleted  $workoutName", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    private fun isNotTextNumberOrSymbol(editValue: EditText): String {
        var value: String = editValue.text.toString().trim()
        val findSymbol: Pattern =
            Pattern.compile("[1234567890`~!@#$%^&*()+=;΄¨':\"\\\\/.,|<>?{}\\[\\]-]")
        val inspectChar: Matcher = findSymbol.matcher(value)
        val checkForSymbol: Boolean = inspectChar.find()

        return if (checkForSymbol) {
            WidgetsWarnings.editTextWarning(editValue, "No symbols or numbers")
            ""
        } else {
            value = value.replace(" ", "_")
            value
        }
    }

    private fun isTextEmpty(editValue: EditText): String{
        val value: String = editValue.text.toString().trim()
        return value.ifEmpty {
            WidgetsWarnings.editTextWarning(editValue, "Enter Name")
            ""
        }
    }

    private fun isNotDuplicate(editValue: EditText, workoutDB: WorkoutDB): String {
        val value: String = editValue.text.toString().trim()
        val checkForDuplicate = workoutDB.loadWorkoutTableNames()
        for(name in checkForDuplicate){
            if(value == name){
                WidgetsWarnings.editTextWarning(editValue, "Workout Exists")
                return ""
            }
        }
        return value
    }
}