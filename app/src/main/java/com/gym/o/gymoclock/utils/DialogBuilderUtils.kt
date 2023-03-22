package com.gym.o.gymoclock.utils

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.databases.ExercisesScreenDB
import com.gym.o.gymoclock.databases.WorkoutDB


class DialogBuilderUtils(context: Context) : AlertDialog.Builder(context) {

    /****************************************/
    /*            WORKOUT DIALOGS           */
    /****************************************/

    private var dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
    lateinit var dialog: AlertDialog
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private val viewAddOrEditExercise: View = inflater.inflate(R.layout.dialog_edit_workout, null)
    private val inflaterRecycler: LayoutInflater = LayoutInflater.from(context)
    val viewRecycler: View = inflaterRecycler.inflate(R.layout.add_recycler_view, null)


    val exerciseNameEdit: EditText = viewAddOrEditExercise.findViewById(R.id.exercise_name_edit)
    var workDigitalTime: TextView = viewAddOrEditExercise.findViewById(R.id.work_digital_time)
    val restDigitalTime: TextView = viewAddOrEditExercise.findViewById(R.id.rest_digital_time)


    val workTimePicker: Button = viewAddOrEditExercise.findViewById(R.id.work_time_picker)
    val restTimePicker: Button = viewAddOrEditExercise.findViewById(R.id.rest_time_picker)

    val verifyButtonEditExercise: ImageButton = viewAddOrEditExercise.findViewById(R.id.ok_button)
    val cancelButtonEditExercise: ImageButton = viewAddOrEditExercise.findViewById(R.id.cancel_button)

    private val viewRemoveExercise: View = inflater.inflate(R.layout.dialog_delete_exercise, null)
    val okButtonRemoveExercise: Button = viewRemoveExercise.findViewById(R.id.verify_exercise_delete)
    val cancelButtonRemoveExercise: Button = viewRemoveExercise.findViewById(R.id.cancel_exercise_delete)

    fun addOrEditExerciseDialog(setCanceledOnTouchOutside: Boolean) {

        exerciseNameEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                Log.i("Editable", TimePickerUtils.timePicked.toString())
                if (s.toString().isNotEmpty())
                    verifyButtonEditExercise.background.setTintList(ContextCompat.getColorStateList(context, R.color.custom_text_color))
                else if (s.toString().isEmpty() && !TimePickerUtils.timePicked)
                    verifyButtonEditExercise.background.setTintList(ContextCompat.getColorStateList(context, R.color.grayed_icons))
            }
        })

        dialogBuilder.setView(viewAddOrEditExercise)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
        dialog.show()
    }

    fun removeExerciseDialog(setCanceledOnTouchOutside: Boolean) {
        dialogBuilder.setView(viewRemoveExercise)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
        dialog.show()
    }

    /****************************************/
    /*          CALENDAR DIALOGS            */
    /****************************************/

    private val viewCalendarWorkoutDetails: View = inflater.inflate(R.layout.dialog_calendar_workout_details, null)
    private val calendarCircleChangeButton: ImageButton = viewCalendarWorkoutDetails.findViewById(R.id.calendar_change_to_exercises_button)
    private val calendarCancelButton: ImageButton = viewCalendarWorkoutDetails.findViewById(R.id.calendar_cancel_button)
    private val scrollView: HorizontalScrollView = viewCalendarWorkoutDetails.findViewById(R.id.scroll_view_change)

    private val tableLayout: View = inflater.inflate(R.layout.table_layout_workout_details, null)
    val calendarDate: TextView = tableLayout.findViewById(R.id.calendar_date)
    val calendarStartTime: TextView = tableLayout.findViewById(R.id.calendar_start_time)
    val calendarEndTime: TextView = tableLayout.findViewById(R.id.calendar_end_time)
    val calendarWorkoutName: TextView = tableLayout.findViewById(R.id.calendar_workout_name)
    val calendarTotalTime: TextView = tableLayout.findViewById(R.id.calendar_total_time)
    val calendarTotalWorkingTime: TextView = tableLayout.findViewById(R.id.calendar_total_working_time)
    private var tableLayoutInflater: View = inflater.inflate(R.layout.table_layout_inflater, null)

    fun calendarWorkoutDetailsDialog(setCanceledOnTouchOutside: Boolean) {

        dialogBuilder.setView(viewCalendarWorkoutDetails)
        scrollView.addView(tableLayout)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
        dialog.show()
    }

    private var viewSpecifyDetailsLayout = inflater.inflate(R.layout.dialog_exercise_screen_list, null)
    private var isViewOnGenericWorkoutDetails = true
    private var isViewOnExerciseDetails = true

    fun onClickListenerCalendarScope(bookName: String, tableName: String, workoutName: String, workoutID: String, sets: Int) {

        calendarCircleChangeButton.setOnClickListener {
            isViewOnGenericWorkoutDetails = !isViewOnGenericWorkoutDetails

            scrollView.removeAllViews()

            if (isViewOnGenericWorkoutDetails) {
                calendarCircleChangeButton.background.setTint(ContextCompat.getColor(context, R.color.custom_text_color))
                scrollView.addView(tableLayout)
            }

            if (!isViewOnGenericWorkoutDetails) {

                if (isViewOnExerciseDetails) {

                    val exercisesScreenDB = ExercisesScreenDB(context)
                    Log.i("KEYS_DIALOG", "$bookName $tableName")
                    if (!exercisesScreenDB.readDataList(bookName, tableName, viewSpecifyDetailsLayout)) {
                        val viewNoDetailsFound: View = inflater.inflate(R.layout.dialog_no_details_found, null)
                        val noDetailsTextView: TextView = viewNoDetailsFound.findViewById(R.id.no_details_tv)
                        noDetailsTextView.text = "${noDetailsTextView.text}\n$workoutName"
                        val insertDetailsButton = viewNoDetailsFound.findViewById<Button>(R.id.insert_details)
                        scrollView.addView(viewNoDetailsFound)

                        insertDetailsButton.setOnClickListener {
                            dialog.dismiss()
                            screeningDBDetailsDialog(bookName, tableName, workoutName, workoutID, sets)
                        }

                        return@setOnClickListener
                    }

                }

                isViewOnExerciseDetails = !isViewOnExerciseDetails
                calendarCircleChangeButton.background.setTint(Color.WHITE)
                scrollView.addView(viewSpecifyDetailsLayout)
            }
        }

        calendarCancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    /****************************************/
    /*     EXERCISE SCREENING DIALOGS       */
    /****************************************/
    private val viewScreeningDB: View = inflater.inflate(R.layout.dialog_verify_for_workout_screening, null)
    val okButtonVerifyDetails: Button = viewScreeningDB.findViewById(R.id.verify_details_screening)
    val cancelButtonVerifyDetails: Button = viewScreeningDB.findViewById(R.id.cancel_details_screening)

    fun screeningDBDialog(setCanceledOnTouchOutside: Boolean) {
        dialogBuilder.setView(viewScreeningDB)
        //dialogBuilder.setCancelable(false)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
        dialog.show()
    }


    private var viewSpecifyDetails: View = inflater.inflate(R.layout.dialog_specify_workout_details, null)
    private var scrollViewSpecifyDetails: ScrollView = viewSpecifyDetails.findViewById(R.id.scroll_view_add_details)
    private var table: TableLayout = tableLayoutInflater.findViewById(R.id.show_details_table)
    private var nextSetButtonSpecifyDetails: ImageButton = viewSpecifyDetails.findViewById(R.id.screeningDB_button_next_set)
    private var roundsCountText = 1
    private val treeMapUtils = TreeMapUtils()
    fun screeningDBDetailsDialog(bookName: String?, tableName: String?, workoutName: String?, workoutID: String?, sets: Int?) {

        val layoutIndicators: LinearLayout = viewSpecifyDetails.findViewById(R.id.layout_round_indicator)

        val resources = context.resources
        val lp: TableRow.LayoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
        lp.setMargins(0, 0, 0, 0)

        val roundsText = TextView(context)
        roundsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
        roundsText.setTextColor(ResourcesCompat.getColor(resources, R.color.custom_text_color, null))
        roundsText.textAlignment = View.TEXT_ALIGNMENT_CENTER
        roundsText.gravity = View.TEXT_ALIGNMENT_CENTER


        val loadWorkoutExercises: ArrayList<String>
        if (bookName == null) {
            roundsText.text = "Round $roundsCountText/${SharedPreferencesUtils.getRoundsValueFromPreferences(context)}"
            loadWorkoutExercises = treeMapUtils.loadCurrentWorkoutExercises(context)
        } else {
            roundsText.text = "Round $roundsCountText/$sets"
            loadWorkoutExercises = treeMapUtils.loadScreenedWorkoutExercises(context, tableName!!, workoutID!!)
        }

        layoutIndicators.addView(roundsText)

        /*
        * i = 0  → Apply to row: Exercise Name, Weight, Reps
        * i >= 1 → Apply to row: All the names from WorkoutDB */
        /** @see WorkoutDB **/

        val textSize = 14f
        for (i in 0..loadWorkoutExercises.size) {
            val row = TableRow(context)
            row.layoutParams = lp
            row.gravity = Gravity.START

            if (i == 0) {
                val exerciseNameText = TextView(context)
                exerciseNameText.tag = "exercise_name"
                exerciseNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
                exerciseNameText.setTextColor(Color.WHITE)
                exerciseNameText.text = "Exercise Name"
                exerciseNameText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                treeMapUtils.exerciseNameStringList.add(exerciseNameText.text.toString())
                row.addView(exerciseNameText)

                val roundWeightText = TextView(context)
                roundWeightText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
                roundWeightText.setTextColor(Color.WHITE)
                roundWeightText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                roundWeightText.text = "Weight"
                treeMapUtils.roundWeightStringList.add(roundWeightText.text.toString())
                row.addView(roundWeightText)

                val roundRepsText = TextView(context)
                roundRepsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
                roundRepsText.setTextColor(Color.WHITE)
                roundRepsText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                roundRepsText.text = "Reps"
                treeMapUtils.roundRepsStringList.add(roundRepsText.text.toString())
                row.addView(roundRepsText)

            } else {

                val exerciseNameText = TextView(context)
                exerciseNameText.tag = "exercise_name_text_$i"
                exerciseNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
                exerciseNameText.setTextColor(Color.WHITE)
                exerciseNameText.text = loadWorkoutExercises[i - 1]
                exerciseNameText.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                treeMapUtils.exerciseNameStringList.add(exerciseNameText.text.toString())
                row.addView(exerciseNameText)

                val editTextWeight = EditText(context)
                editTextWeight.tag = "edit_text_weight_$i"
                editTextWeight.textAlignment = View.TEXT_ALIGNMENT_CENTER
                editTextWeight.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                editTextWeight.filters += InputFilter.LengthFilter(7) // set max length
                editTextWeight.hint = "max 999"
                FormatUtils.textChangeListenerDecimal(context, editTextWeight, null, nextSetButtonSpecifyDetails)
                treeMapUtils.roundWeightEditTexts.add(editTextWeight)
                row.addView(editTextWeight)

                val editTextReps = EditText(context)
                editTextReps.tag = "edit_text_rep_$i"
                editTextReps.textAlignment = View.TEXT_ALIGNMENT_CENTER
                editTextReps.inputType = InputType.TYPE_CLASS_NUMBER
                editTextReps.filters += InputFilter.LengthFilter(3)
                editTextReps.hint = "max 999"
                FormatUtils.textChangeListenerInteger(context, editTextReps, null, nextSetButtonSpecifyDetails)
                treeMapUtils.roundRepsEditTexts.add(editTextReps)
                row.addView(editTextReps)

            }
            table.addView(row, i)
        }

        val lastRow = TableRow(context)
        lastRow.gravity = Gravity.START
        lastRow.layoutParams = lp

        val applyToAllText = TextView(context)
        applyToAllText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
        applyToAllText.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
        applyToAllText.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        applyToAllText.text = "Apply To All"
        lastRow.addView(applyToAllText)

        val weightApplyToAll = EditText(context)
        weightApplyToAll.tag = "weight_apply_to_all"
        weightApplyToAll.textAlignment = View.TEXT_ALIGNMENT_CENTER
        weightApplyToAll.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        weightApplyToAll.filters += InputFilter.LengthFilter(7)
        weightApplyToAll.hint = "weight"
        FormatUtils.textChangeListenerDecimal(context, weightApplyToAll, treeMapUtils.roundWeightEditTexts, nextSetButtonSpecifyDetails)

        lastRow.addView(weightApplyToAll)

        val repsApplyToAll = EditText(context)
        repsApplyToAll.tag = "reps_apply_to_all"
        repsApplyToAll.textAlignment = View.TEXT_ALIGNMENT_CENTER
        repsApplyToAll.inputType = InputType.TYPE_CLASS_NUMBER
        repsApplyToAll.filters += InputFilter.LengthFilter(3)
        repsApplyToAll.hint = "reps"
        FormatUtils.textChangeListenerInteger(context, repsApplyToAll, treeMapUtils.roundRepsEditTexts, nextSetButtonSpecifyDetails)

        lastRow.addView(repsApplyToAll)

        table.addView(lastRow)

        scrollViewSpecifyDetails.addView(tableLayoutInflater)
        dialogBuilder.setCancelable(false)
        dialogBuilder.setView(viewSpecifyDetails)
        dialog = dialogBuilder.create()
        dialog.show()

        screeningDBDetailsOnClickListenerScope(bookName, tableName, workoutName, workoutID, sets)
    }

    private fun screeningDBDetailsOnClickListenerScope(bookName: String?, tableName: String?, workoutName: String?, workoutID: String?, sets: Int?) {

        val setsCount = if (bookName == null)
            SharedPreferencesUtils.getRoundsValueFromPreferences(context)
        else
            sets

        if (roundsCountText == setsCount)
            nextSetButtonSpecifyDetails.setBackgroundResource(R.drawable.ic_check_change)

        nextSetButtonSpecifyDetails.setOnClickListener {
            if (!treeMapUtils.isHashMapPrepared())
                return@setOnClickListener
            else
                treeMapUtils.addToTreeMap(roundsCountText)

            if (roundsCountText == setsCount) {
                val exercisesScreenDB = ExercisesScreenDB(context)
                exercisesScreenDB.saveHashMapDetailsOnEndWorkout(context, bookName, treeMapUtils)
                treeMapUtils.instantiateLists()
                roundsCountText = 1
                dialog.dismiss()
                return@setOnClickListener
            }
            roundsCountText++
            treeMapUtils.instantiateLists()
            instantiateViews()
            dialog.dismiss()
            screeningDBDetailsDialog(bookName, tableName, workoutName, workoutID, sets)
        }

    }

    private fun instantiateViews() {
        tableLayoutInflater = inflater.inflate(R.layout.table_layout_inflater, null)
        table = tableLayoutInflater.findViewById(R.id.show_details_table)
        viewSpecifyDetails = inflater.inflate(R.layout.dialog_specify_workout_details, null)
        nextSetButtonSpecifyDetails = viewSpecifyDetails.findViewById(R.id.screeningDB_button_next_set)
        scrollViewSpecifyDetails = viewSpecifyDetails.findViewById(R.id.scroll_view_add_details)
    }


    /****************************************/
    /*          VOICE ASSIST DIALOGS        */
    /****************************************/

    private val viewVoiceAssistSettings: View = inflater.inflate(R.layout.dialog_voice_assist, null)
    var cancelButtonVoiceAssistSettings: ImageButton = viewVoiceAssistSettings.findViewById(R.id.voice_assist_cancel_button)
    var voiceAssistState: SwitchCompat = viewVoiceAssistSettings.findViewById(R.id.voice_assist_state)
    var volumeControl: SeekBar = viewVoiceAssistSettings.findViewById(R.id.volume_control)
    val volumePercent: TextView = viewVoiceAssistSettings.findViewById(R.id.volume_percent)
    var testVoiceVolumeButton: Button = viewVoiceAssistSettings.findViewById(R.id.test_volume_button)

    fun voiceAssistScope(setCanceledOnTouchOutside: Boolean) {
        dialogBuilder.setView(viewVoiceAssistSettings)
        scrollView.addView(tableLayout)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
        dialog.show()
    }


    /****************************************/
    /*           ANIMATIONS DIALOGS         */
    /****************************************/

    private val viewAnimationsSettings: View = inflater.inflate(R.layout.dialog_animations, null)
    var cancelButtonAnimationsSettings: ImageButton = viewAnimationsSettings.findViewById(R.id.animations_cancel_button)
    var allAnimationsState: SwitchCompat = viewAnimationsSettings.findViewById(R.id.all_animations)
    var recyclerViewAnimationsState: SwitchCompat = viewAnimationsSettings.findViewById(R.id.recycler_view_animations)
    var clocksAnimationsState: SwitchCompat = viewAnimationsSettings.findViewById(R.id.clocks_animations)

    fun animationsScope(setCanceledOnTouchOutside: Boolean) {
        dialogBuilder.setView(viewAnimationsSettings)
        scrollView.addView(tableLayout)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
        dialog.show()
    }

}