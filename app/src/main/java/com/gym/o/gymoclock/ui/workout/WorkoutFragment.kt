package com.gym.o.gymoclock.ui.workout

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
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
import com.gym.o.gymoclock.functionality.workout_pr.countdown_functionality.*
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseElements
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseRecyclerAdapter
import com.gym.o.gymoclock.functionality.workout_pr.recycler_items_swipe.setItemTouchHelper
import com.gym.o.gymoclock.functionality.workout_pr.rounds_picker.roundsPicker
import com.gym.o.gymoclock.functionality.workout_pr.rounds_picker.roundsPickerDisabled
import com.gym.o.gymoclock.interfaces.RecyclerViewInterface
import com.gym.o.gymoclock.utils.*
import kotlin.math.abs


open class WorkoutFragment : DialogFragment(), RecyclerViewInterface {
    val TAG_NUMPICKER = "NumberPickerDialogs"

    private var _binding: FragmentWorkoutBinding? = null
    val binding get() = _binding!!

    private var isTimerRunning: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Log.i("WorkoutFragment", "onCreateView")
        init()
        onSavedInstance(savedInstanceState)
        if (workoutTableName.isNotEmpty()) {
            loadRecyclerViews()
            binding.totalTime.text = FormatUtils.convertTimeToDigitalClock((listAdapter.totalTimeFromDB(rounds)).toString())
        }

        setItemTouchHelper()

        recyclerView.smoothScrollToPosition(recyclerPosition)

        workoutInit()

        if (listAdapter.itemCount != 0)
            roundsPicker()

        binding.workoutWorkoutName.text = FormatUtils.stringUnderscoreToSpace(workoutTableName)

        getLastPositionForAddViewAnimation = -1
        getLastPositionForRemoveViewAnimation = -1

        return root
    }


    override fun onDestroyView() {

        // _binding = null
        //TextToSpeechUtils.getInstance(requireContext()).stopTTS()
//        binding.addLayout.isEnabled = false
//        binding.addLayout.isClickable = false
//
//        binding.playPauseButton.isEnabled = false
//        binding.playPauseButton.isClickable = false
//
//        binding.roundsEdit.isEnabled = false
//        binding.roundsEdit.isClickable = false
//
//        if(listAdapter.itemCount > 0) {
//            val position = dataList[recyclerPosition]
//
//            if (position.wTimerIsRunning) {
//                pauseTotalTimer()
//                listAdapter.pauseExerciseTimer(recyclerPosition, resources.getString(R.string.workout_stopped))
//            }
//
//            if (position.rTimerIsRunning) {
//                pauseTotalTimer()
//                listAdapter.pauseRestTimer(recyclerPosition, resources.getString(R.string.workout_stopped))
//            }
//        }
//        recyclerPosition = 0
//        PrepareTimerState.prepareTimerState = PrepareTimerStateEnum.Preparing
//        Log.i("WorkFrag", "onDestroyView")
//        stopAnimation(ClockSelected.clockSelected)
        super.onDestroyView()

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

        if(listAdapter.itemCount > 0) {
            val position = dataList[recyclerPosition]

            if (position.wTimerIsRunning) {
                pauseTotalTimer()
                listAdapter.pauseExerciseTimer(recyclerPosition, resources.getString(R.string.workout_stopped))
                stopAnimation(ClockSelected.clockSelected)
            }

            if (position.rTimerIsRunning) {
                pauseTotalTimer()
                listAdapter.pauseRestTimer(recyclerPosition, resources.getString(R.string.workout_stopped))
                stopAnimation(ClockSelected.clockSelected)
            }
        }
        recyclerPosition = 0

        Log.i("WorkFrag", "onDestroyView")



        _binding = null
//        Log.i("WorkFrag", "onDestroy")

    }


    lateinit var dialogBuilderUtils: DialogBuilderUtils
    private lateinit var timePickerUtils: TimePickerUtils

    override fun editExercise(dataPosition: Int) {
        val position = dataList[dataPosition]
        dialogBuilderUtils = DialogBuilderUtils(requireContext())
        dialogBuilderUtils.addOrEditExercise(false)
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

        dialogBuilderUtils.removeExercise(false)

        Log.e("WorkoutFrag", "PositionData: $dataPosition")

        dialogBuilderUtils.okButtonRemoveExercise.setOnClickListener {
            val deleteView = workoutDB.deleteWorkoutDetails(workoutTableName, position.exerciseNameValue)
            if (deleteView)
                Toast.makeText(context, "Exercise Deleted", Toast.LENGTH_SHORT).show()

            if (SharedPreferencesUtils.getRecyclerViewAnimationsState(requireContext()))
                listAdapter.setOnRemoveViewAnimation(itemView, dataPosition)
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    dataList.removeAt(dataPosition)
                    //listAdapter.notifyItemRemoved(dataPosition)
                    //listAdapter.notifyDataSetChanged()
                }, 500)

            getLastPositionForAddViewAnimation = -1
            getLastPositionForRemoveViewAnimation = -1

            Log.e("WorkoutFrag", "Item Position = $dataPosition, Animate Delete Position = $getLastPositionForRemoveViewAnimation" + " " +
                    "Animate Add Position = $getLastPositionForAddViewAnimation")

            binding.totalTime.text = FormatUtils.convertTimeToDigitalClock((listAdapter.totalTimeFromDB(rounds)).toString())
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    loadRecyclerViews()
                    //Log.i("RMV Exercise", "Item count: ${listAdapter.itemCount}")
                    if (listAdapter.itemCount == 0) {
                        Log.i("RMV Exercise", "Item count: ${listAdapter.itemCount}")
                        roundsPickerDisabled()
                    }
                }, 500)



            dialogBuilderUtils.dialog.dismiss()
        }
        dialogBuilderUtils.cancelButtonRemoveExercise.setOnClickListener { dialogBuilderUtils.dialog.dismiss() }
    }

    private var animRotationAngle = 0f
    private var durationTime = 0L
    override fun animateClock(clockSelected: ClockSelectedEnum, animDuration: Long) {
        if (SharedPreferencesUtils.getClocksAnimationsState(requireContext())) {
            val holder: ExerciseRecyclerAdapter.ViewHolder? = (recyclerView.adapter as ExerciseRecyclerAdapter).getViewByPosition(recyclerPosition)
            println("HOLDER: $holder $recyclerPosition")

            lateinit var view: ImageView
            when (clockSelected) {
                ClockSelectedEnum.WorkClock -> view = holder!!.exerciseImg
                ClockSelectedEnum.RestClock -> view = holder!!.restImg
                ClockSelectedEnum.Idle      -> {}
            }

            Log.i("DurationTime", "$durationTime")
            if(durationTime == 0L)
                durationTime = animDuration

            Log.i("DurationTime", "$durationTime")
            Log.i("AnimationDuration", "$animDuration")
            //view.rotation = animRotationAngle
            //animRotationAngle = abs(view.rotation)

            val degrees = (durationTime - animDuration)/1000
            if (view.rotation > 360f) {
                Log.i("degrees", "$degrees")
                view.rotation = view.rotation / degrees
            }
            animRotationAngle = view.rotation

            view.animate().apply {
                duration = animDuration
                interpolator = LinearInterpolator()
                rotationBy((360f - animRotationAngle) * (animDuration / 1000))
            }.start()
            //view.rotation = 0f
            //view.animate().rotationBy((360f - animRotationAngle)*(animDuration/1000)).setDuration(animDuration).setInterpolator(LinearInterpolator()).start()

            Log.i("AnimRotationAngle", "$animRotationAngle")
            Log.i("RotationValue", "${abs((360f - animRotationAngle) * (animDuration / 1000))}")

        }
    }

    override fun stopAnimation(clockSelected: ClockSelectedEnum) {
        if (SharedPreferencesUtils.getClocksAnimationsState(requireContext())) {
            val holder: ExerciseRecyclerAdapter.ViewHolder? = (recyclerView.adapter as ExerciseRecyclerAdapter).getViewByPosition(recyclerPosition)
            lateinit var view: ImageView
            when (clockSelected) {
                ClockSelectedEnum.WorkClock -> view = holder!!.exerciseImg
                ClockSelectedEnum.RestClock -> view = holder!!.restImg
                ClockSelectedEnum.Idle      -> {}
            }

            view.animate().cancel()
            view.clearAnimation()
            animRotationAngle = view.rotation
        }
    }

    private lateinit var db: SQLiteDatabase
    lateinit var workCountDown: CountDownTimer
    lateinit var restCountDown: CountDownTimer

    override fun loadRecyclerViews() {
        workoutDB = WorkoutDB(requireActivity().applicationContext)
        db = workoutDB.readableDatabase
        recyclerView.recycledViewPool.clear()
        recyclerView.layoutManager = WrapContentLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        //prevents recycler views from stacking
        dataList.clear()
        //recyclerView.layoutManager = WrapContentLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
        listAdapter.notifyDataSetChanged()

        if (listAdapter.itemCount > 0) {
            lastRestTimeCheck()
            roundsPicker()
        }
        animRotationAngle = 0f
    }


    override fun roundsCount() {
        rounds--
        recyclerPosition = 0
        val lastRecyclerPosition = dataList[listAdapter.itemCount - 1]
        binding.roundsPicker.value = rounds
        Log.d("MAIN", "Rounds Total: $rounds, isStartWorkout $isTimerRunning -- ${DateTimeUtils.getCurrentTime()}")
        Log.d("MAIN", "iterator = $recyclerPosition rounds = $rounds -- ${DateTimeUtils.getCurrentTime()}")
        if (workoutTableName == "")
            return

        if (rounds == 0) {
            binding.roundsPicker.textColor = Color.RED
            binding.roundsPicker.value = 1
            onEndOfWorkout()
            return
        }

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
        recyclerView = binding.recyclerView//findViewById(R.id.recycler_view)
        listAdapter = ExerciseRecyclerAdapter(requireContext(), this, dataList)

        recyclerView.apply {
            val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(getDrawable(context, R.drawable.divider_recycler_view)!!)
            addItemDecoration(dividerItemDecoration)
        }
        binding.swipeRefreshRecycler.setOnRefreshListener {
            if (!isTimerRunning) {
                getLastPositionForAddViewAnimation = -1
                getLastPositionForRemoveViewAnimation = -1
                loadRecyclerViews()
            }
            binding.swipeRefreshRecycler.isRefreshing = false
        }
//        val itemTouchHelper = ItemTouchHelper(itemTouchHelper)
//        itemTouchHelper.attachToRecyclerView(recyclerView)

        workoutDB = WorkoutDB(requireActivity().applicationContext)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        recyclerView.adapter = listAdapter
    }


    private fun workoutInit() {
        binding.addLayout.setOnClickListener {
            if (workoutTableName.isEmpty()) {
                Toast.makeText(context, "No exercises available. Please add an exercise in order to start.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            addExerciseRecyclerView()
            return@setOnClickListener
        }

        binding.playPauseButton.setOnClickListener {
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

            isTimerRunning = !isTimerRunning
            Log.d("isStartWorkout", (isTimerRunning).toString())

            if (isTimerRunning) {

                if (PrepareTimerState.prepareTimerState == PrepareTimerStateEnum.Preparing) {
                    prepareForWorkoutTimer()
                    return@setOnClickListener
                }

                binding.playPauseButton.background = getDrawable(requireContext(), R.drawable.ic_pause_button)
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
            binding.playPauseButton.background = getDrawable(requireContext(), R.drawable.ic_play_button)
            if (SharedPreferencesUtils.getClocksAnimationsState(requireContext()))
                stopAnimation(ClockSelected.clockSelected)
            return@setOnClickListener
        }
    }

    private lateinit var calendarDB: CalendarDB
    private lateinit var exercisesScreenDB: ExercisesScreenDB
    private fun onEndOfWorkout() {
        binding.playPauseButton.background = getDrawable(requireContext(), R.drawable.ic_play_button)
        isTimerRunning = false
        PrepareTimerState.prepareTimerState = PrepareTimerStateEnum.Preparing
        buttonsStateOnWorkout(true)

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

        dialogBuilderUtils.addOrEditExercise(false)

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
        if (rounds <= 1) {
            lastRecyclerPosition.restClockValue.text = FormatUtils.convertTimeToDigitalClock("0")
            listAdapter.notifyDataSetChanged()
            Log.d(TAG_NUMPICKER, "AFTER ROUNDS CHANGED = ${binding.totalTime.text}")
        } else {
            lastRecyclerPosition.restClockValue.text = FormatUtils.convertTimeToDigitalClock(
                workoutDB.lastRestTime(workoutTableName).toString()
            )
            listAdapter.notifyDataSetChanged()
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong("prepareCountdownInMillis", prepareCountdownInMillis)
        outState.putBoolean("isPrepareCountdown", isPrepareCountdown)

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
            isPrepareCountdown = savedInstanceState.getBoolean("isPrepareCountdown", true)

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

}