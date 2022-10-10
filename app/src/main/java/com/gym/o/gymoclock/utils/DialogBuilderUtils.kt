package com.gym.o.gymoclock.utils

import android.content.Context
import android.graphics.Color
import android.text.Editable
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
import com.gym.o.gymoclock.functionality.calendar_pr.*


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

    private val viewRemoveExercise: View = inflater.inflate(R.layout.delete_exercise, null)
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

    fun calendarWorkoutDetailsDialog(setCanceledOnTouchOutside: Boolean) {

        dialogBuilder.setView(viewCalendarWorkoutDetails)
        scrollView.addView(tableLayout)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
        dialog.show()
    }

    private var viewSpecifyDetailsLayout = inflater.inflate(R.layout.dialog_exercise_screen_list, null)
    private var isViewOnWorkoutDetails = true
    private var isListForLoad = true

    fun onClickListenerCalendarScope(bookName: String, tableName: String) {

        calendarCircleChangeButton.setOnClickListener {
            isViewOnWorkoutDetails = !isViewOnWorkoutDetails

            scrollView.removeAllViews()

            if (isViewOnWorkoutDetails) {
                calendarCircleChangeButton.background.setTint(ContextCompat.getColor(context, R.color.custom_text_color))
                scrollView.addView(tableLayout)
            }

            if (!isViewOnWorkoutDetails) {
                if (isListForLoad) {
                    val exercisesScreenDB = ExercisesScreenDB(context)
                    exercisesScreenDB.readDataList(bookName, tableName, viewSpecifyDetailsLayout)
                }

                isListForLoad = false
                calendarCircleChangeButton.background.setTint(Color.WHITE)
                scrollView.addView(viewSpecifyDetailsLayout)
            }
        }

        calendarCancelButton.setOnClickListener {
            dialog.dismiss()
        }
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

    /****************************************/
    /*     EXERCISE SCREENING DIALOGS       */
    /****************************************/
    private val viewScreeningDB: View = inflater.inflate(R.layout.dialog_verify_for_workout_screening, null)
    val okButtonVerifyDetails: Button = viewScreeningDB.findViewById(R.id.verify_details_screening)
    val cancelButtonVerifyDetails: Button = viewScreeningDB.findViewById(R.id.cancel_details_screening)

    fun screeningDBDialog(setCanceledOnTouchOutside: Boolean) {
        dialogBuilder.setView(viewScreeningDB)
        dialogBuilder.setCancelable(false)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
        dialog.show()
    }


    private var viewSpecifyDetails: View = inflater.inflate(R.layout.dialog_specify_workout_details, null)

    fun screeningDBDetailsDialog() {
        val table: TableLayout = viewSpecifyDetails.findViewById(R.id.specify_details_table)
        val resources = context.resources
        val lp: TableRow.LayoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)

        val textSize = 14f
        val roundsText = TextView(context)
        roundsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
        roundsText.setTextColor(ResourcesCompat.getColor(resources, R.color.custom_text_color, null))
        roundsText.textAlignment = View.TEXT_ALIGNMENT_CENTER
        roundsText.text = "Round $roundsCountText/${SharedPreferencesUtils.getRoundsValueFromPreferences(context)}"

        val rowRounds = TableRow(context)
        rowRounds.gravity = Gravity.CENTER
        rowRounds.layoutParams = lp
        rowRounds.addView(roundsText)
        table.addView(rowRounds)

        /*
        * i = 0  → Apply to row: Rounds count
        * i = 1  → Apply to row: Exercise Name, Weight, Reps
        * i >= 2 → Apply to row: All the names from WorkoutDB */
        /** @see WorkoutDB **/

        for (i in 1..loadWorkoutExercises(context).size + 1) {
            val row = TableRow(context)
            row.layoutParams = lp
            row.gravity = Gravity.START

            if (i == 1) {
                val exerciseNameText = TextView(context)
                exerciseNameText.tag = "exercise_name"
                exerciseNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
                exerciseNameText.setTextColor(Color.WHITE)
                exerciseNameText.text = "Exercise Name"
                exerciseNameText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                exerciseNameStringList.add(exerciseNameText.text.toString())
                row.addView(exerciseNameText)

                val roundWeightText = TextView(context)
                roundWeightText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
                roundWeightText.setTextColor(Color.WHITE)
                roundWeightText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                roundWeightText.text = "Weight"
                roundWeightStringList.add(roundWeightText.text.toString())
                row.addView(roundWeightText)

                val roundRepsText = TextView(context)
                roundRepsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
                roundRepsText.setTextColor(Color.WHITE)
                roundRepsText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                roundRepsText.text = "Reps"
                roundRepsStringList.add(roundRepsText.text.toString())
                row.addView(roundRepsText)

            } else {

                val exerciseNameText = TextView(context)
                exerciseNameText.tag = "exercise_name_text_$i"
                exerciseNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
                exerciseNameText.setTextColor(Color.WHITE)
                exerciseNameText.text = loadWorkoutExercises(context)[i - 2]
                exerciseNameText.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                exerciseNameStringList.add(exerciseNameText.text.toString())
                row.addView(exerciseNameText)

                val editTextWeight = EditText(context)
                editTextWeight.tag = "edit_text_weight_$i"
                editTextWeight.textAlignment = View.TEXT_ALIGNMENT_CENTER
                editTextWeight.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                editTextWeight.hint = "max 999"
                roundWeightEditTexts.add(editTextWeight)
                row.addView(editTextWeight)

                val editTextReps = EditText(context)
                editTextReps.tag = "edit_text_rep_$i"
                editTextReps.textAlignment = View.TEXT_ALIGNMENT_CENTER
                editTextReps.inputType = InputType.TYPE_CLASS_NUMBER
                editTextReps.hint = "max 999"
                roundRepsEditTexts.add(editTextReps)
                row.addView(editTextReps)

            }
            table.addView(row, i)
        }

        dialogBuilder.setView(viewSpecifyDetails)
        dialog = dialogBuilder.create()
        dialog.show()
        screeningDBDetailsOnClickListenerScope()
    }


    private var nextSetButtonSpecifyDetails: Button = viewSpecifyDetails.findViewById(R.id.screeningDB_button_next_set)

    private fun screeningDBDetailsOnClickListenerScope() {

        if (roundsCountText == SharedPreferencesUtils.getRoundsValueFromPreferences(context))
            nextSetButtonSpecifyDetails.text = "SET"

        nextSetButtonSpecifyDetails.setOnClickListener {

            if (!isHashMapPrepared())
                return@setOnClickListener

            if (roundsCountText == SharedPreferencesUtils.getRoundsValueFromPreferences(context)) {
                saveHashMapDetails(context)
                dialog.dismiss()
                return@setOnClickListener
            }

            instantiateLists()
            roundsCountText++
            viewSpecifyDetails = inflater.inflate(R.layout.dialog_specify_workout_details, null)
            nextSetButtonSpecifyDetails = viewSpecifyDetails.findViewById(R.id.screeningDB_button_next_set)
            dialog.dismiss()
            screeningDBDetailsDialog()
        }

    }


}




