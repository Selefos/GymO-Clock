package com.gym.o.gymoclock.utils

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
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
import com.gym.o.gymoclock.databases.CalendarDB
import com.gym.o.gymoclock.databases.ExercisesScreenDB
import com.gym.o.gymoclock.databases.WorkoutDB
import com.gym.o.gymoclock.functionality.calendar_pr.ExerciseScreeningAdapter
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseScreening
import com.gym.o.gymoclock.functionality.workout_pr.workoutTableName
import io.paperdb.Paper


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
                    readDataList(bookName, tableName)
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

    private lateinit var keysList: ArrayList<ExerciseScreening>
    private var viewSpecifyDetailsLayout = inflater.inflate(R.layout.dialog_exercise_screen_list, null)

    fun readDataList(bookName: String, keyName: String) {

        val listView = viewSpecifyDetailsLayout.findViewById<ListView>(R.id.exercise_details_list)
        val data = Paper.book(keyName).read<HashMap<String, ArrayList<ArrayList<String>>>>(bookName)

        keysList = ArrayList()
        var exerciseName: String

        for (index in 0 until data?.get("Round1")!![0].size - 1) {
            exerciseName = data["Round1"]!![0][index + 1]
            keysList.add(ExerciseScreening(exerciseName, index))
        }

        val exerciseScreeningAdapter = ExerciseScreeningAdapter(context, R.layout.dialog_exercise_screen_list_layout, keysList, bookName, keyName)
        listView.adapter = exerciseScreeningAdapter

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
    private var roundsCountText = 1
    private val roundWeightEditTexts = arrayListOf<EditText>()
    private val roundRepsEditTexts = arrayListOf<EditText>()

    private val exerciseNameStringList = arrayListOf<String>()
    private var roundWeightStringList = arrayListOf<String>()
    private var roundRepsStringList = arrayListOf<String>()

    private val screeningDetailsHashMap: HashMap<String, ArrayList<ArrayList<String>>> = HashMap()

    private fun loadWorkoutExercises(): ArrayList<String> {
        val workoutDB = WorkoutDB(context)
        val db: SQLiteDatabase = workoutDB.readableDatabase
        val cursorWorkoutDB = workoutDB.loadRecyclerElements(workoutTableName, db)
        val exerciseNameList = arrayListOf<String>()

        if (cursorWorkoutDB.moveToFirst()) {
            do {
                exerciseNameList.add(cursorWorkoutDB.getString(1))
                println(exerciseNameList)
            } while (cursorWorkoutDB.moveToNext())
        }
        cursorWorkoutDB.close()
        db.close()

        return exerciseNameList
    }

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
        loadWorkoutExercises()
        for (i in 1..loadWorkoutExercises().size + 1) {
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
                exerciseNameText.text = loadWorkoutExercises()[i - 2]
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
    private var detailsArray = arrayListOf<ArrayList<String>>()
    private fun screeningDBDetailsOnClickListenerScope() {

        if (roundsCountText == SharedPreferencesUtils.getRoundsValueFromPreferences(context))
            nextSetButtonSpecifyDetails.text = "SET"

        nextSetButtonSpecifyDetails.setOnClickListener {

            if (!isHashMapPrepared())
                return@setOnClickListener

            if (roundsCountText == SharedPreferencesUtils.getRoundsValueFromPreferences(context)) {
                saveHashMapDetails()
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

    private fun isHashMapPrepared(): Boolean {
        var isEditTextReady = true
        for (i in roundWeightEditTexts.indices) {

            if (ErrorUtils.isTextEmpty(roundWeightEditTexts[i]).isEmpty()) {
                isEditTextReady = false
            } else
                roundWeightStringList.add(roundWeightEditTexts[i].text.toString())

            if (ErrorUtils.isTextEmpty(roundRepsEditTexts[i]).isEmpty()) {
                isEditTextReady = false
            } else
                roundRepsStringList.add(roundRepsEditTexts[i].text.toString())
//            roundWeightStringList.add((0..25).random().toString())
//            roundRepsStringList.add((0..25).random().toString())
        }

        detailsArray.add(exerciseNameStringList)
        detailsArray.add(roundWeightStringList)
        detailsArray.add(roundRepsStringList)
        screeningDetailsHashMap["Round$roundsCountText"] = detailsArray

        return isEditTextReady
    }

    private fun saveHashMapDetails() {
        val calendarDB = CalendarDB(context)
        val sqlDB: SQLiteDatabase = calendarDB.readableDatabase
        val tableName = "${(DateTimeUtils.getCurrentMonth())}_${DateTimeUtils.getCurrentYear()}"
        println(tableName)
        val cursor: Cursor = calendarDB.getCalendarWorkoutID(tableName, DateTimeUtils.getDate(), sqlDB)
        //val cursor: Cursor = calendarDB.getCalendarWorkoutID("SEPTEMBER_2022", "30-09-2022", sqlDB)

        val exercisesScreenDB = ExercisesScreenDB(context)
        if (cursor.moveToLast()) {
            exercisesScreenDB.saveData("${DateTimeUtils.getDate()}_${cursor.getString(0)}", screeningDetailsHashMap)
            //ExercisesScreenDB.readData("${DateTimeUtils.getDate()}_${cursor.getString(0)}")
        }

        cursor.close()
        sqlDB.close()
    }

    private fun instantiateLists() {
        exerciseNameStringList.clear()
        roundWeightEditTexts.clear()
        roundRepsEditTexts.clear()
        roundWeightStringList = ArrayList()
        roundRepsStringList = ArrayList()
        detailsArray = ArrayList()
    }

}




