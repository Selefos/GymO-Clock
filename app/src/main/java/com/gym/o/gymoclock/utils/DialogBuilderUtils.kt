package com.gym.o.gymoclock.utils

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.gym.o.gymoclock.R


class DialogBuilderUtils(context: Context) : AlertDialog.Builder(context) {

    private var dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
    lateinit var dialog: AlertDialog
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private val viewAddOrEditExercise: View = inflater.inflate(R.layout.dialog_edit_workout, null)
    private val inflaterRecycler: LayoutInflater = LayoutInflater.from(context)
    val viewRecycler: View = inflaterRecycler.inflate(R.layout.add_view, null)

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


    fun addOrEditExercise(setCanceledOnTouchOutside: Boolean) {

        exerciseNameEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                Log.i("Editable", TimePickerUtils.timePicked.toString())
                if(s.toString().isNotEmpty())
                    verifyButtonEditExercise.background.setTintList(ContextCompat.getColorStateList(context, R.color.custom_text_color))
                else if(s.toString().isEmpty() && !TimePickerUtils.timePicked)
                    verifyButtonEditExercise.background.setTintList(ContextCompat.getColorStateList(context, R.color.grayed_icons))
            }
        })

        dialogBuilder.setView(viewAddOrEditExercise)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
        dialog.show()
    }

    fun removeExercise(setCanceledOnTouchOutside: Boolean) {
        dialogBuilder.setView(viewRemoveExercise)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
        dialog.show()
    }

    private val viewCalendarWorkoutDetails: View = inflater.inflate(R.layout.dialog_calendar_workout_details, null)
    private val calendarCircleChangeButton: ImageButton = viewCalendarWorkoutDetails.findViewById(R.id.calendar_change_to_exercises_button)
    private val calendarCancelButton: ImageButton = viewCalendarWorkoutDetails.findViewById(R.id.calendar_cancel_button)
    private val scrollView: ScrollView = viewCalendarWorkoutDetails.findViewById(R.id.scroll_view_change)

    private val tableLayout: View = inflater.inflate(R.layout.table_layout_workout_details, null)
    val calendarDate: TextView = tableLayout.findViewById(R.id.calendar_date)
    val calendarStartTime: TextView = tableLayout.findViewById(R.id.calendar_start_time)
    val calendarEndTime: TextView = tableLayout.findViewById(R.id.calendar_end_time)
    val calendarWorkoutName: TextView = tableLayout.findViewById(R.id.calendar_workout_name)
    val calendarTotalTime: TextView = tableLayout.findViewById(R.id.calendar_total_time)
    val calendarTotalWorkingTime: TextView = tableLayout.findViewById(R.id.calendar_total_working_time)

    fun calendarWorkoutDetails(setCanceledOnTouchOutside: Boolean) {

        dialogBuilder.setView(viewCalendarWorkoutDetails)
        scrollView.addView(tableLayout)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
        dialog.show()
    }


    private val viewCalendarExerciseScreenList = inflater.inflate(R.layout.dialog_exercise_screen_list, null)//findViewById(R.id.dialog_exercise_screen_list) as View
    val exerciseScreenListLayout: LinearLayout = viewCalendarExerciseScreenList.findViewById(R.id.exercise_screen_list)
    private var isViewOnWorkoutDetails = true
    private var isListForLoad = true
    
    fun onClickListenerCalendarScope(workoutID: String, populateTextViews: (workoutID: String) -> Unit){

        calendarCircleChangeButton.setOnClickListener{
            isViewOnWorkoutDetails = !isViewOnWorkoutDetails


            scrollView.removeAllViews()
            Log.i("IsOnDetails", isViewOnWorkoutDetails.toString())
            Log.i("FLAG", isListForLoad.toString())
            if(isViewOnWorkoutDetails){
                calendarCircleChangeButton.background.setTint(ContextCompat.getColor(context, R.color.custom_text_color))
                scrollView.addView(tableLayout)
            }

            if(!isViewOnWorkoutDetails) {
                if(isListForLoad)
                    populateTextViews(workoutID)
                isListForLoad = false
                calendarCircleChangeButton.background.setTint(Color.WHITE)
                scrollView.addView(viewCalendarExerciseScreenList)
            }
        }

        calendarCancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    private val viewVoiceAssistSettings: View = inflater.inflate(R.layout.dialog_voice_assist, null)
    var cancelButtonVoiceAssistSettings: ImageButton = viewVoiceAssistSettings.findViewById(R.id.voice_assist_cancel_button)
    var voiceAssistState: SwitchCompat = viewVoiceAssistSettings.findViewById(R.id.voice_assist_state)
    var volumeControl: SeekBar = viewVoiceAssistSettings.findViewById(R.id.volume_control)
    val volumePercent: TextView = viewVoiceAssistSettings.findViewById(R.id.volume_percent)
    var testVoiceVolumeButton: Button = viewVoiceAssistSettings.findViewById(R.id.test_volume_button)

    fun voiceAssistScope(setCanceledOnTouchOutside: Boolean){
        dialogBuilder.setView(viewVoiceAssistSettings)
        scrollView.addView(tableLayout)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
        dialog.show()
    }
}