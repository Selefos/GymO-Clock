package com.gym.o.gymoclock.functionality.main_activity_pr.settings

import android.util.Log
import com.gym.o.gymoclock.MainActivity
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.utils.DialogBuilderUtils
import com.gym.o.gymoclock.utils.SharedPreferencesUtils

fun MainActivity.animations(){
    val dialogBuilderUtils = DialogBuilderUtils(this)
    dialogBuilderUtils.animationsScope(false)

    dialogBuilderUtils.cancelButtonAnimationsSettings.setOnClickListener {
        dialogBuilderUtils.dialog.dismiss()
    }

    allAnimationsState(dialogBuilderUtils)
    recyclerViewAnimationsState(dialogBuilderUtils)
    clocksAnimationsState(dialogBuilderUtils)
}

fun MainActivity.allAnimationsState(dialogBuilderUtils: DialogBuilderUtils){
    dialogBuilderUtils.allAnimationsState.isChecked = SharedPreferencesUtils.getAllAnimationsState(this)

    if (dialogBuilderUtils.allAnimationsState.isChecked) {
        dialogBuilderUtils.allAnimationsState.text = resources.getString(R.string.settings_disable_all_animations)
    }
    else {
        dialogBuilderUtils.allAnimationsState.text = resources.getString(R.string.settings_enable_all_animations)
        isSwitchEnabled(dialogBuilderUtils, dialogBuilderUtils.allAnimationsState.isChecked)
    }

    animRecyclerViewState(dialogBuilderUtils, dialogBuilderUtils.allAnimationsState.isChecked)
    animClocksState(dialogBuilderUtils, dialogBuilderUtils.allAnimationsState.isChecked)

    dialogBuilderUtils.allAnimationsState.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            dialogBuilderUtils.allAnimationsState.text = resources.getString(R.string.settings_disable_all_animations)
            isSwitchEnabled(dialogBuilderUtils, isChecked)
            Log.i("IsChecked", isChecked.toString())
        } else {
            dialogBuilderUtils.allAnimationsState.text = resources.getString(R.string.settings_enable_all_animations)
            Log.i("IsChecked", isChecked.toString())

            animRecyclerViewState(dialogBuilderUtils, isChecked)
            SharedPreferencesUtils.saveLayoutAnimationsState(this, isChecked)

            animClocksState(dialogBuilderUtils, isChecked)
            SharedPreferencesUtils.saveClocksAnimationsState(this, isChecked)

            isSwitchEnabled(dialogBuilderUtils, isChecked)
        }
        SharedPreferencesUtils.saveAllAnimationsState(this, dialogBuilderUtils.allAnimationsState.isChecked)

    }
}

fun MainActivity.recyclerViewAnimationsState(dialogBuilderUtils: DialogBuilderUtils){
    animRecyclerViewState(dialogBuilderUtils,SharedPreferencesUtils.getLayoutAnimationsState(this))

    dialogBuilderUtils.recyclerViewAnimationsState.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            dialogBuilderUtils.recyclerViewAnimationsState.text = resources.getString(R.string.settings_disable_recycler_view_animations)
            Log.i("IsChecked", isChecked.toString())
        } else {
            dialogBuilderUtils.recyclerViewAnimationsState.text = resources.getString(R.string.settings_enable_recycler_view_animations)
            Log.i("IsChecked", isChecked.toString())
        }
        SharedPreferencesUtils.saveLayoutAnimationsState(this, dialogBuilderUtils.recyclerViewAnimationsState.isChecked)
    }
}

fun MainActivity.clocksAnimationsState(dialogBuilderUtils: DialogBuilderUtils){

    animClocksState(dialogBuilderUtils, SharedPreferencesUtils.getClocksAnimationsState(this))

    dialogBuilderUtils.clocksAnimationsState.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            dialogBuilderUtils.clocksAnimationsState.text = resources.getString(R.string.settings_disable_clock_animations)
            Log.i("IsChecked", isChecked.toString())
        } else {
            dialogBuilderUtils.clocksAnimationsState.text = resources.getString(R.string.settings_enable_clock_animations)
            Log.i("IsChecked", isChecked.toString())
        }
        SharedPreferencesUtils.saveClocksAnimationsState(this, dialogBuilderUtils.clocksAnimationsState.isChecked)

    }
}

fun MainActivity.animRecyclerViewState(dialogBuilderUtils: DialogBuilderUtils, isChecked: Boolean){
    dialogBuilderUtils.recyclerViewAnimationsState.isChecked = isChecked
    if (isChecked)
        dialogBuilderUtils.recyclerViewAnimationsState.text = resources.getString(R.string.settings_disable_recycler_view_animations)
    else
        dialogBuilderUtils.recyclerViewAnimationsState.text = resources.getString(R.string.settings_enable_recycler_view_animations)

}

fun MainActivity.animClocksState(dialogBuilderUtils: DialogBuilderUtils, isChecked: Boolean){
    dialogBuilderUtils.clocksAnimationsState.isChecked = isChecked
    if (isChecked)
        dialogBuilderUtils.clocksAnimationsState.text = resources.getString(R.string.settings_disable_clock_animations)
    else
        dialogBuilderUtils.clocksAnimationsState.text = resources.getString(R.string.settings_enable_clock_animations)
}

fun isSwitchEnabled(dialogBuilderUtils: DialogBuilderUtils, isEnabled: Boolean){
    dialogBuilderUtils.recyclerViewAnimationsState.isEnabled = isEnabled
    dialogBuilderUtils.clocksAnimationsState.isEnabled = isEnabled
    Log.i("isEnabled", isEnabled.toString())
}