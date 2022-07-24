package com.gym.o.gymoclock.functionality.main_activity_pr

import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.GravityCompat
import com.gym.o.gymoclock.MainActivity
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.databases.WorkoutDB
import com.gym.o.gymoclock.functionality.common.workout_db_calls.addWorkoutTable
import com.gym.o.gymoclock.functionality.common.workout_db_calls.editWorkoutName
import com.gym.o.gymoclock.functionality.workout_pr.navigation_list_adapter.CustomExpandableListAdapter
import com.gym.o.gymoclock.functionality.workout_pr.navigation_list_adapter.MenuModel
import com.gym.o.gymoclock.ui.calendar.CalendarFragment
import com.gym.o.gymoclock.ui.settings.SettingsFragment
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import com.gym.o.gymoclock.utils.FormatUtils
import com.gym.o.gymoclock.utils.SharedPreferencesUtils


private var listIcon: MutableList<MenuModel> = ArrayList()
private var listTitle: MutableList<MenuModel> = ArrayList()
private var listChild = HashMap<MenuModel, List<MenuModel>?>()
//private lateinit var listIcon: ImageView

fun MainActivity.setupDrawer() {
    drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.closed)
    drawerLayout.addDrawerListener(drawerToggle)
    drawerToggle.syncState()

}


fun MainActivity.prepareMenuData() {

    listTitle.clear()
    workoutDB = WorkoutDB(this)
    val workoutNames: List<String> = workoutDB.loadWorkoutTableNames()
    var childModelsList: MutableList<MenuModel> = ArrayList()
    var childModel: MenuModel

    var menuModel = MenuModel(AppCompatResources.getDrawable(this,R.drawable.ic_menu_twindumbbells)!!, "Workout")//"Workout"//MenuModel("Workout") //Menu of Android Tutorial. No sub menus
    listTitle.add(menuModel)

    for (i in workoutNames) {
        childModel = MenuModel(AppCompatResources.getDrawable(this,R.drawable.ic_menu_dumbbell)!!, i.replace("_", " "))
        childModelsList.add(childModel)
    }
    //if (menuModel.hasChildren) {
    listChild[menuModel] = childModelsList
    //}
    //childModelsList = ArrayList()

    menuModel = MenuModel(AppCompatResources.getDrawable(this,R.drawable.ic_menu_add)!!,"Add Workout")//"Add Workout"
    listTitle.add(menuModel)

    menuModel = MenuModel(AppCompatResources.getDrawable(this,R.drawable.ic_menu_calendar)!!,"Calendar")
    listTitle.add(menuModel)

    menuModel = MenuModel(AppCompatResources.getDrawable(this,R.drawable.ic_menu_settings)!!,"Settings")//"Settings"
    listTitle.add(menuModel)
//    val listImage = resources.getDrawable(R.drawable.ic_menu_subitem)//findViewById<ImageView>(R.drawable.ic_menu_subitem)
//    listIcon.add(listImage)
//    listIcon.add(listImage)
//    listIcon.add(listImage)
//    listIcon.add(listImage)
//listIcon = findViewById(R.drawable.ic_menu_subitem)


}


fun MainActivity.populateList() {
    expandableAdapter = CustomExpandableListAdapter(this, listIcon, listTitle, listChild)
    expandableListView.setAdapter(expandableAdapter)

    expandableListView.setOnGroupClickListener { parent, v, groupPosition, id ->
        when (id) {
            1L -> addWorkoutTable()
            2L -> { drawerLayout.closeDrawer(GravityCompat.START)
                changeFragment(CalendarFragment::class.java)
            }
            3L -> {  drawerLayout.closeDrawer(GravityCompat.START)
                changeFragment(SettingsFragment::class.java)
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

    expandableListView.setOnChildClickListener(ExpandableListView.OnChildClickListener(fun(parent: ExpandableListView, v: View, groupPosition: Int,
        childPosition: Int, id: Long): Boolean {

        val selectedItem = listChild[listTitle[groupPosition]]?.get(childPosition)?.menuName
        supportActionBar?.title = childPosition.toString()

        supportActionBar?.title = selectedItem
        val handler = Handler()
        changeNavHeaderText(selectedItem!!)
        handler.postDelayed(Runnable { drawerLayout.closeDrawer(GravityCompat.START) }, 500)
        //drawerLayout.closeDrawer(GravityCompat.START)
        when (selectedItem) {
            selectedItem -> {

                SharedPreferencesUtils.saveWorkoutTableNameToPreferences(this, selectedItem)
                //setWorkoutTableName(FormatUtils.prepareWorkoutTableStringSpDs(selectedItem))

                changeFragment(WorkoutFragment::class.java)
            }
        }
        return true
    }))

    expandableListView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
//            if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
//                // You now have everything that you would as if this was an OnChildClickListener()
//                // Add your logic here.
//                // Return true as we are handling the event.
//                when (id) {
//                    0L -> addWorkoutTable()
//                }
//                return@OnItemLongClickListener true
//            }

            if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                val groupPosition = ExpandableListView.getPackedPositionGroup(id)
                val childPosition = ExpandableListView.getPackedPositionChild(id)
                val selectedItem = listChild[listTitle[groupPosition]]?.get(childPosition)?.menuName

                editWorkoutName(FormatUtils.stringSpaceToUnderscore(selectedItem!!))
                return@OnItemLongClickListener true
            }

            false
        }
}


fun MainActivity.changeNavHeaderText(workoutName: String) {
    val headerView = binding.navView.getHeaderView(0)//navigationView.getHeaderView(0)
    val navUsername = headerView.findViewById(R.id.exercise_name_navigation_view) as TextView
    navUsername.text = FormatUtils.stringUnderscoreToSpace(workoutName)
}