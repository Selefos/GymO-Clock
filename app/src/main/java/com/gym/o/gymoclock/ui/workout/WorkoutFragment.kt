package com.gym.o.gymoclock.ui.workout

import android.content.res.Resources
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.databases.CalendarDB
import com.gym.o.gymoclock.databases.ExercisesScreenDB
import com.gym.o.gymoclock.databases.WorkoutDB
import com.gym.o.gymoclock.databinding.FragmentWorkoutBinding
import com.gym.o.gymoclock.enums.ClockSelected
import com.gym.o.gymoclock.enums.ClockSelectedEnum
import com.gym.o.gymoclock.enums.PrepareTimerState
import com.gym.o.gymoclock.enums.PrepareTimerStateEnum
import com.gym.o.gymoclock.functionality.common.workout_db_calls.addEditExercise
import com.gym.o.gymoclock.functionality.common.workout_db_calls.updateExerciseValues
import com.gym.o.gymoclock.functionality.workout_pr.*
import com.gym.o.gymoclock.functionality.workout_pr.animations.foldSettingsAnimation
import com.gym.o.gymoclock.functionality.workout_pr.animations.setFABPosition
import com.gym.o.gymoclock.functionality.workout_pr.animations.setOnRemoveViewAnimation
import com.gym.o.gymoclock.functionality.workout_pr.countdown_functionality.*
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseElements
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseRecyclerAdapter
import com.gym.o.gymoclock.functionality.workout_pr.rounds_picker.roundsPicker
import com.gym.o.gymoclock.functionality.workout_pr.rounds_picker.roundsPickerDisabled
import com.gym.o.gymoclock.interfaces.RecyclerViewInterface
import com.gym.o.gymoclock.utils.*


open class WorkoutFragment : DialogFragment(), RecyclerViewInterface {
    val TAG_NUMPICKER = "NumberPickerDialogs"

    private var _binding: FragmentWorkoutBinding? = null
    val binding get() = _binding!!

    private var isTimerRunning: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init()
        onSavedInstance(savedInstanceState)
        if (workoutTableName.isNotEmpty()) {
            loadRecyclerViews()
            binding.totalTime.text = FormatUtils.convertTimeToDigitalClock((listAdapter.totalTimeFromDB(rounds)).toString())
        }

        //setItemTouchHelper()

        recyclerView.smoothScrollToPosition(recyclerPosition)

        if (listAdapter.itemCount != 0)
            roundsPicker()

        binding.workoutWorkoutName.text = FormatUtils.stringUnderscoreToSpace(workoutTableName)

        getLastPositionForAddViewAnimation = -1
        getLastPositionForRemoveViewAnimation = -1

        Log.i

        return root
    }


    override fun onDestroy() {
        super.onDestroy()
//        TextToSpeechUtils.getInstance(requireContext()).stopTTS()
//        binding.addLayout.isEnabled = false
//        binding.addLayout.isClickable = false
//
//        binding.playPauseButton.isEnabled = false
//        binding.playPauseButton.isClickable = false
//
//        binding.roundsEdit.isEnabled = false
//        binding.roundsEdit.isClickable = false
//

        if (listAdapter.itemCount > 0) {
            val position = dataList[recyclerPosition]

            if (position.wTimerIsRunning) {
                pauseTotalTimer()
                listAdapter.pauseExerciseTimer(recyclerPosition, resources.getString(R.string.workout_stopped))
            }

            if (position.rTimerIsRunning) {
                pauseTotalTimer()
                listAdapter.pauseRestTimer(recyclerPosition, resources.getString(R.string.workout_stopped))
            }
        }
        recyclerPosition = 0
        PrepareTimerState.prepareTimerState = PrepareTimerStateEnum.Preparing
        Log.i("WorkFrag", "onDestroyView")



        _binding = null
//        Log.i("WorkFrag", "onDestroy")

    }


    lateinit var dialogBuilderUtils: DialogBuilderUtils
    private lateinit var timePickerUtils: TimePickerUtils

    override fun editExercise(dataPosition: Int) {
        val position = dataList[dataPosition]
        dialogBuilderUtils = DialogBuilderUtils(requireContext())
        dialogBuilderUtils.addOrEditExerciseDialog(false)
        timePickerUtils = TimePickerUtils(requireContext())

        dialogBuilderUtils.workDigitalTime.text = position.exerciseClockValue.text
        dialogBuilderUtils.restDigitalTime.text = position.restClockValue.text

//        dialogBuilderUtils.onClickListenerWorkoutScope(
//            { updateExerciseValues(dataPosition) },
//            { timePickerUtils.numberPickerTimeDialogExercises(dialogBuilderUtils.workDigitalTime, dialogBuilderUtils.verifyButtonEditExercise) },
//            { timePickerUtils.numberPickerTimeDialogExercises(dialogBuilderUtils.restDigitalTime, dialogBuilderUtils.verifyButtonEditExercise) }
//        )

        dialogBuilderUtils.verifyButtonEditExercise.setOnClickListener {
            updateExerciseValues(dataPosition)
        }
        dialogBuilderUtils.cancelButtonEditExercise.setOnClickListener {
            TimePickerUtils.isTimePicked(false)
            dialogBuilderUtils.dialog.dismiss()
        }
        dialogBuilderUtils.workTimePicker.setOnClickListener {
            timePickerUtils.numberPickerTimeDialogExercises(dialogBuilderUtils.workDigitalTime, dialogBuilderUtils.verifyButtonEditExercise)
        }
        dialogBuilderUtils.restTimePicker.setOnClickListener {
            timePickerUtils.numberPickerTimeDialogExercises(dialogBuilderUtils.restDigitalTime, dialogBuilderUtils.verifyButtonEditExercise)
        }

    }


    override fun removeExercise(itemView: View, dataPosition: Int) {
        workoutDB = WorkoutDB(requireContext())
        dialogBuilderUtils = DialogBuilderUtils(requireContext())
        val position = dataList[dataPosition]

        dialogBuilderUtils.removeExerciseDialog(false)

        Log.e("WorkoutFrag", "PositionData: $dataPosition")

        dialogBuilderUtils.okButtonRemoveExercise.setOnClickListener {
            val deleteView = workoutDB.deleteWorkoutDetails(workoutTableName, position.exerciseNameValue)
            if (deleteView)
                Toast.makeText(context, "Exercise Deleted", Toast.LENGTH_SHORT).show()

            if (SharedPreferencesUtils.getRecyclerViewAnimationsState(requireContext()))
                setOnRemoveViewAnimation(itemView, dataPosition)

            Handler(Looper.getMainLooper()).postDelayed(
                {
                    dataList.removeAt(dataPosition)
                    listAdapter.notifyItemRemoved(dataPosition)
                }, 500)

//            dataList.removeAt(dataPosition)
//            listAdapter.notifyItemRemoved(dataPosition)

            getLastPositionForAddViewAnimation = -1
            getLastPositionForRemoveViewAnimation = -1

            Log.e("WorkoutFrag", "Item Position = $dataPosition, Animate Delete Position = $getLastPositionForRemoveViewAnimation" + " " +
                    "Animate Add Position = $getLastPositionForAddViewAnimation")

            binding.totalTime.text = FormatUtils.convertTimeToDigitalClock((listAdapter.totalTimeFromDB(rounds)).toString())
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    loadRecyclerViews()
                    if (listAdapter.itemCount == 0)
                        roundsPickerDisabled()
                }, 600)

            dialogBuilderUtils.dialog.dismiss()
        }
        dialogBuilderUtils.cancelButtonRemoveExercise.setOnClickListener { dialogBuilderUtils.dialog.dismiss() }
    }


    override fun startClockProgressBar(clockSelected: ClockSelectedEnum, animDuration: Long) {
        if (SharedPreferencesUtils.getClocksAnimationsState(requireContext())) {
            val holder: ExerciseRecyclerAdapter.ViewHolder? = (recyclerView.adapter as ExerciseRecyclerAdapter).getViewByPosition(recyclerPosition)
            println("HOLDER: $holder $recyclerPosition")

            lateinit var view: ProgressBar
            when (clockSelected) {
                ClockSelectedEnum.WorkClock -> view = holder!!.exerciseProgress
                ClockSelectedEnum.RestClock -> view = holder!!.restProgress
                ClockSelectedEnum.Idle      -> {}
            }

//            val progress = FormatUtils.convertLongToInt(durationTime) - FormatUtils.convertLongToInt(animDuration)
//            Log.i("ProgressBar", "$progress")
//            view.max = FormatUtils.convertLongToInt(durationTime)
//            if(progress <= view.max)
//                view.progress = FormatUtils.convertLongToInt(durationTime) - FormatUtils.convertLongToInt(animDuration)

            view.setBigMax(10)
            view.animateTo(10, 100, animDuration)

        }
    }


    override fun stopClockProgressBar(clockSelected: ClockSelectedEnum) {
        if (SharedPreferencesUtils.getClocksAnimationsState(requireContext())) {
            val holder: ExerciseRecyclerAdapter.ViewHolder? = (recyclerView.adapter as ExerciseRecyclerAdapter).getViewByPosition(recyclerPosition)
            println("HOLDER: $holder $recyclerPosition")
            lateinit var view: ProgressBar
            when (clockSelected) {
                ClockSelectedEnum.WorkClock -> view = holder!!.exerciseProgress
                ClockSelectedEnum.RestClock -> view = holder!!.restProgress
                ClockSelectedEnum.Idle      -> {}
            }

            view.pauseAnimation()
        }
    }


    private lateinit var db: SQLiteDatabase
    lateinit var workCountDown: CountDownTimer
    lateinit var restCountDown: CountDownTimer
    override fun loadRecyclerViews() {
        workoutDB = WorkoutDB(requireActivity().applicationContext)
        db = workoutDB.readableDatabase

        resetProgressBar()
        removeAllRecyclerViews()

        //prevents recycler views from stacking
        dataList.clear()
        recyclerView.recycledViewPool.clear()
        recyclerView.layoutManager = WrapContentLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val cursor: Cursor = workoutDB.loadRecyclerElements(workoutTableName, db)

        if (cursor.moveToFirst()) {
            do {
                val inflaterRecycler = layoutInflater
                val viewRecycler = inflaterRecycler.inflate(R.layout.add_view, null)

                val exerciseName: TextView = viewRecycler.findViewById(R.id.exercise_name)
                val exerciseClock: TextView = viewRecycler.findViewById(R.id.countdown_work)
                val restClock: TextView = viewRecycler.findViewById(R.id.countdown_rest)

                workCountDown = object : CountDownTimer(0, 1000) {
                    override fun onTick(millsUntilFinish: Long) {
                        workTimeInMillis = millsUntilFinish
                    }

                    override fun onFinish() {}
                }
                restCountDown = object : CountDownTimer(0, 1000) {
                    override fun onTick(millsUntilFinish: Long) {
                        restTimeInMillis = millsUntilFinish
                    }

                    override fun onFinish() {}
                }

                exerciseName.text = cursor.getString(1)
                exerciseClock.text = FormatUtils.convertTimeToDigitalClock(cursor.getInt(2).toString())
                restClock.text = FormatUtils.convertTimeToDigitalClock(cursor.getInt(3).toString())

                dataList.add(
                    ExerciseElements(
                        exerciseName.text.toString(), exerciseClock, restClock,
                        workCountDown, restCountDown,
                        wTimerIsRunning = false, wTimerIsPaused = false,
                        rTimerIsRunning = false, rTimerIsPaused = false
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        //listAdapter.notifyDataSetChanged()

        if (listAdapter.itemCount > 0) {
            lastRestTimeCheck()
            roundsPicker()
        }

    }


    override fun roundsCount() {
        rounds--
        recyclerPosition = 0
        binding.roundsPicker.value = rounds
        Log.d("MAIN", "Rounds Total: $rounds, isStartWorkout $isTimerRunning -- ${DateTimeUtils.getCurrentTime()}")
        Log.d("MAIN", "iterator = $recyclerPosition rounds = $rounds -- ${DateTimeUtils.getCurrentTime()}")
        if (workoutTableName == "")
            return

        if (rounds == 0) {
            binding.roundsPicker.textColor = Color.RED
            binding.roundsPicker.value = 1
            rounds = 1
            onEndOfWorkout()
            return
        }

        val lastRecyclerPosition = dataList[listAdapter.itemCount - 1]
        if (rounds == 1) {
            lastRecyclerPosition.restClockValue.text = FormatUtils.convertTimeToDigitalClock("0")
            Log.d(TAG_NUMPICKER, "AFTER ROUNDS CHANGED = ${binding.totalTime.text}")
        }

        Log.d("MAIN", "Start Timer Position = $recyclerPosition rounds = $rounds -- ${DateTimeUtils.getCurrentTime()}")
        listAdapter.startExerciseTimer(recyclerPosition)
    }


    override fun scrollToPosition() {
        recyclerView.smoothScrollToPosition(recyclerPosition)
    }


    lateinit var dialogBuilder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    lateinit var recyclerView: RecyclerView
    lateinit var listAdapter: ExerciseRecyclerAdapter
    lateinit var dataList: ArrayList<ExerciseElements>
    lateinit var workoutDB: WorkoutDB

    private fun init() {
        if (SharedPreferencesUtils.getRoundsValueFromPreferences(requireContext()) != null)
            rounds = SharedPreferencesUtils.getRoundsValueFromPreferences(requireContext())
        if (SharedPreferencesUtils.getPrepareTimeFromPreferences(requireContext()) != null)
            prepareCountdownInMillis = SharedPreferencesUtils.getPrepareTimeFromPreferences(requireContext())

        dataList = ArrayList()
        recyclerView = binding.recyclerView
        listAdapter = ExerciseRecyclerAdapter(requireContext(), this, dataList)

        recyclerView.apply {
            val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(getDrawable(context, R.drawable.divider_recycler_view)!!)
            addItemDecoration(dividerItemDecoration)
        }

        binding.swipeRefreshRecycler.setOnRefreshListener {
            if (PrepareTimerState.prepareTimerState == PrepareTimerStateEnum.Preparing) {
                getLastPositionForAddViewAnimation = -1
                getLastPositionForRemoveViewAnimation = -1
                loadRecyclerViews()
            }
            binding.swipeRefreshRecycler.isRefreshing = false
        }

        workoutDB = WorkoutDB(requireActivity().applicationContext)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        recyclerView.adapter = listAdapter

        workoutInit()
        setFABPosition(binding.playPauseFab,
            SharedPreferencesUtils.getPlayPauseFABPositionX(requireContext()),
            SharedPreferencesUtils.getPlayPauseFABPositionY(requireContext())
        )

        setFABPosition(binding.addLayoutFab,
            SharedPreferencesUtils.getAddLayoutFABPositionX(requireContext()),
            SharedPreferencesUtils.getAddLayoutFABPositionY(requireContext())
        )

    }


    private fun workoutInit() {

        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        binding.playPauseFab.setOnTouchListener(object : View.OnTouchListener {
            var x = 0f
            var y = 0f
            var xyPivot = 25

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                val touchDuration = event.eventTime - event.downTime
                val clickThreshold = 200

                var newX = 0f
                var newY = 0f

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x = event.x
                        y = event.y
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        newX = binding.playPauseFab.x + (event.x - x)
                        newY = binding.playPauseFab.y + (event.y - y)
                        // check if the view out of screen
                        // xyPivot is used to prevent dragged view from coming too close to
                        // the screen borders, as results from the view getting bellow it
                        if ((newX <= xyPivot || newX >= screenWidth - view.width - xyPivot) || (newY <= xyPivot || newY >= screenHeight - view.height - xyPivot))
                            return true

                        binding.playPauseFab.x = newX
                        binding.playPauseFab.y = newY

                        SharedPreferencesUtils.savePlayPauseFABPositionXY(requireContext(), newX, newY)

                        return true
                    }
                    MotionEvent.ACTION_UP   -> {
                        if (event.action == MotionEvent.ACTION_UP && touchDuration < clickThreshold)
                            view.performClick()

                        return true
                    }

                }
                return true
            }
        })

        binding.playPauseFab.setOnClickListener {

            if (rounds == 0) {
                recyclerPosition = 0
                WidgetsWarningsUtils.pickerTextWarning(binding.roundsPicker, rounds)
                return@setOnClickListener
            }

            if (listAdapter.itemCount == 0 || listAdapter.totalTimeFromDB(rounds) == 0) {
                binding.totalTimeTextView.setTextColor(Color.RED)
                WidgetsWarningsUtils.textViewWarning(binding.totalTime)
                return@setOnClickListener
            }

            if (binding.roundsPicker.textColor == Color.RED) {
                binding.totalTime.text = FormatUtils.convertTimeToDigitalClock((listAdapter.totalTimeFromDB(rounds)).toString())
                binding.roundsPicker.textColor = Color.WHITE
            }

            isTimerRunning = !isTimerRunning
            Log.d("isStartWorkout", (isTimerRunning).toString())
            if (isTimerRunning) {

                if (PrepareTimerState.prepareTimerState == PrepareTimerStateEnum.Preparing) {
                    exerciseSettingsButtonState(false)
                    prepareForWorkoutTimer()
                    return@setOnClickListener
                }

                binding.playPauseFab.setImageResource(android.R.drawable.ic_media_pause)
                startTotalTimer()
                listAdapter.startExerciseTimer(recyclerPosition)

                return@setOnClickListener
            }

            val position = dataList[recyclerPosition]
            if (position.wTimerIsRunning)
                listAdapter.pauseExerciseTimer(recyclerPosition, resources.getString(R.string.exercise_paused))

            if (position.rTimerIsRunning)
                listAdapter.pauseRestTimer(recyclerPosition, resources.getString(R.string.rest_paused))

            pauseTotalTimer()
            binding.playPauseFab.setImageResource(android.R.drawable.ic_media_play)
            if (SharedPreferencesUtils.getClocksAnimationsState(requireContext()))
                stopClockProgressBar(ClockSelected.clockSelected)

            return@setOnClickListener
        }

        binding.addLayoutFab.setOnTouchListener(object : View.OnTouchListener {
            var x = 0f
            var y = 0f
            var xyPivot = 25

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                val touchDuration = event.eventTime - event.downTime
                val clickThreshold = 200

                var newX = 0f
                var newY = 0f

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x = event.x
                        y = event.y
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        newX = binding.addLayoutFab.x + (event.x - x)
                        newY = binding.addLayoutFab.y + (event.y - y)
                        // check if the view out of screen
                        // xyPivot is used to prevent dragged view from coming too close to
                        // the screen borders, as results from the view getting bellow it
                        if ((newX <= xyPivot || newX >= screenWidth - view.width - xyPivot) || (newY <= xyPivot || newY >= screenHeight - view.height - xyPivot))
                            return true

                        binding.addLayoutFab.x = newX
                        binding.addLayoutFab.y = newY

                        SharedPreferencesUtils.saveAddLayoutFABPositionXY(requireContext(), newX, newY)

                        return true
                    }
                    MotionEvent.ACTION_UP   -> {
                        if (event.action == MotionEvent.ACTION_UP && touchDuration < clickThreshold)
                            view.performClick()

                        return true
                    }

                }
                return true
            }
        })

        binding.addLayoutFab.setOnClickListener {
            if (workoutTableName.isEmpty()) {
                Toast.makeText(context, "No workouts available. Please add an exercise in order to start.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            addExerciseRecyclerView()
            return@setOnClickListener
        }
    }


    private lateinit var calendarDB: CalendarDB
    private lateinit var exercisesScreenDB: ExercisesScreenDB
    private fun onEndOfWorkout() {
        //binding.playPauseButton.background = getDrawable(requireContext(), R.drawable.ic_play_button)
        binding.playPauseFab.setImageResource(android.R.drawable.ic_media_play)
        isTimerRunning = false
        PrepareTimerState.prepareTimerState = PrepareTimerStateEnum.Preparing
        buttonsStateOnWorkout(true)
        exerciseSettingsButtonState(true)

        calendarDB = CalendarDB(context)
        Log.d("CountDown", "rounds == 0 ${DateTimeUtils.getCurrentTime()}")
        val monthYear = "${DateTimeUtils.getCurrentMonth()} ${DateTimeUtils.getCurrentYear()}".replace(" ", "_")

        calendarDB.insertCalendarDetails(
            monthYear, DateTimeUtils.getDate(), startTime, DateTimeUtils.getCurrentTime(), workoutTableName,
            FormatUtils.convertTimeToDigitalClock(listAdapter.totalTimeFromDB(SharedPreferencesUtils.getRoundsValueFromPreferences(requireContext())).toString()),
            FormatUtils.convertTimeToDigitalClock(listAdapter.totalWorkingTime(SharedPreferencesUtils.getRoundsValueFromPreferences(requireContext())).toString())
        )

        saveExercisesList(monthYear)
        ClockSelected.clockSelected = ClockSelectedEnum.Idle
        startTime = ""
    }


    private fun saveExercisesList(monthYear: String) {
        val workoutDB = WorkoutDB(context)
        db = workoutDB.readableDatabase
        val cursorWorkoutDB = workoutDB.loadRecyclerElements(workoutTableName, db)

        var exercisesList = ""
        if (cursorWorkoutDB.moveToFirst()) {
            do {
                exercisesList += cursorWorkoutDB.getString(1)
                if (!cursorWorkoutDB.isLast)
                    exercisesList += FormatUtils.strSeparator
            } while (cursorWorkoutDB.moveToNext())
        }
        cursorWorkoutDB.close()
        db.close()

        exercisesScreenDB = ExercisesScreenDB(context)
        val sqlDB: SQLiteDatabase = calendarDB.readableDatabase
        val cursorCalendarDB: Cursor = calendarDB.getCalendarWorkoutDate(monthYear, workoutTableName, sqlDB)
        cursorCalendarDB.moveToLast()
        exercisesScreenDB.insertScreenDBDetails(cursorCalendarDB.getString(0), cursorCalendarDB.getString(1),
            cursorCalendarDB.getString(2), exercisesList)

        cursorCalendarDB.close()
        sqlDB.close()
    }


    private fun addExerciseRecyclerView() {
        dialogBuilderUtils = DialogBuilderUtils(requireContext())
        timePickerUtils = TimePickerUtils(requireContext())

        dialogBuilderUtils.addOrEditExerciseDialog(false)

//        dialogBuilderUtils.onClickListenerWorkoutScope(
//            { addEditExercise() },
//            { timePickerUtils.numberPickerTimeDialogExercises(dialogBuilderUtils.workDigitalTime, dialogBuilderUtils.verifyButtonEditExercise) },
//            { timePickerUtils.numberPickerTimeDialogExercises(dialogBuilderUtils.restDigitalTime, dialogBuilderUtils.verifyButtonEditExercise) }
//        )

        dialogBuilderUtils.verifyButtonEditExercise.setOnClickListener {
            addEditExercise()
        }
        dialogBuilderUtils.cancelButtonEditExercise.setOnClickListener {
            TimePickerUtils.isTimePicked(false)
            dialogBuilderUtils.dialog.dismiss()
        }
        dialogBuilderUtils.workTimePicker.setOnClickListener {
            timePickerUtils.numberPickerTimeDialogExercises(dialogBuilderUtils.workDigitalTime, dialogBuilderUtils.verifyButtonEditExercise)
        }
        dialogBuilderUtils.restTimePicker.setOnClickListener {
            timePickerUtils.numberPickerTimeDialogExercises(dialogBuilderUtils.restDigitalTime, dialogBuilderUtils.verifyButtonEditExercise)
        }

    }


    fun nameIsDuplicate(name: String): Boolean {
        workoutDB = WorkoutDB(requireActivity().applicationContext)
        val nameTemp: List<String> = workoutDB.checkForDuplicateNames(workoutTableName)

        for (temp in nameTemp)
            if (name == temp)
                return true

        return false
    }


    fun lastRestTimeCheck() {
        val lastRecyclerPosition = dataList[listAdapter.itemCount - 1]
        val lastPosition = listAdapter.itemCount - 1

        if (rounds <= 1) {
            lastRecyclerPosition.restClockValue.text = FormatUtils.convertTimeToDigitalClock("0")
            listAdapter.notifyItemChanged(lastPosition)
            Log.d(TAG_NUMPICKER, "AFTER ROUNDS CHANGED = ${binding.totalTime.text}")
        } else {
            lastRecyclerPosition.restClockValue.text = FormatUtils.convertTimeToDigitalClock(workoutDB.lastRestTime(workoutTableName).toString())
            Log.d(TAG_NUMPICKER, "AFTER ROUNDS CHANGED = ${binding.totalTime.text}")
            listAdapter.notifyItemChanged(lastPosition)
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong("prepareCountdownInMillis", prepareCountdownInMillis)
        //outState.putBoolean("isPrepareCountdown", isPrepareCountdown)

        outState.putBoolean("isStartWorkout", isTimerRunning)
        outState.putInt("iterator", recyclerPosition)
        outState.putInt("rounds", rounds)

        outState.putLong("totalTimeInMillis", totalTimeInMillis)
        outState.putLong("workTimeInMillis", workTimeInMillis)
        outState.putLong("restTimeInMillis", restTimeInMillis)
        outState.putLong("endTime", endTime)
    }


    private fun onSavedInstance(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            prepareCountdownInMillis = savedInstanceState.getLong(
                "prepareCountdownInMillis",
                0)
            //isPrepareCountdown = savedInstanceState.getBoolean("isPrepareCountdown", true)

            isTimerRunning = savedInstanceState.getBoolean("isStartWorkout")
            recyclerPosition = savedInstanceState.getInt("iterator")
            rounds = savedInstanceState.getInt("rounds")

            totalTimeInMillis = savedInstanceState.getLong("totalTimeInMillis")
            workTimeInMillis = savedInstanceState.getLong("workTimeInMillis")
            restTimeInMillis = savedInstanceState.getLong("restTimeInMillis")

            if (dataList[recyclerPosition].wTimerIsRunning) {
                endTime = savedInstanceState.getLong("endTime")
                totalTimeInMillis = endTimeTotalTimer - System.currentTimeMillis()
                workTimeInMillis = endTime - System.currentTimeMillis()
                startTotalTimer()
                listAdapter.startExerciseTimer(recyclerPosition)
            }

            if (dataList[recyclerPosition].rTimerIsRunning) {
                restTimeInMillis = endTime - System.currentTimeMillis()
                startTotalTimer()
                listAdapter.startRestTimer(recyclerPosition)

            }
        } else {
//            prepareCountdownInMillis = 1000
//            isPrepareCountdown = true
//            isStartWorkout = true
//            iterator=0
//
//            rounds=0
//            totalTimeInMillis=0L
//            workTimeInMillis=0L
//            restTimeInMillis=0L
//            endTime=0L
//            totalTimeInMillis=0L
//            workTimeInMillis=0L
//            restTimeInMillis=0L
        }
    }


    private fun resetProgressBar() {
        var holder: ExerciseRecyclerAdapter.ViewHolder?

        for (i: Int in 0 until listAdapter.itemCount) {
            holder = (recyclerView.adapter as ExerciseRecyclerAdapter).getViewByPosition(i)
            holder?.exerciseProgress?.progress = 0
            holder?.restProgress?.progress = 0
        }

    }


    private fun removeAllRecyclerViews() {

        if (listAdapter.itemCount > 0) {
            for (i: Int in 0 until listAdapter.itemCount)
                dataList.removeAt(0)

            listAdapter.notifyDataSetChanged()
        }

    }


    private fun exerciseSettingsButtonState(stateEnabled: Boolean) {

        var holder: ExerciseRecyclerAdapter.ViewHolder?
        for (i: Int in 0 until listAdapter.itemCount) {
            holder = (recyclerView.adapter as ExerciseRecyclerAdapter).getViewByPosition(i)
            holder?.exerciseSettingsButton?.isEnabled = stateEnabled

            if (holder?.isSettingsVisible == true)
                foldSettingsAnimation(requireContext(), holder.exerciseSettingsButton, holder.editView, holder.removeView)

        }

    }

}