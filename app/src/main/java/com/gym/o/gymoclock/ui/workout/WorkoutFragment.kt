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
import com.gym.o.gymoclock.databases.WorkoutDB
import com.gym.o.gymoclock.databinding.FragmentWorkoutBinding
import com.gym.o.gymoclock.functionality.workout_pr.*
import com.gym.o.gymoclock.functionality.workout_pr.countdown_functions.*
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseElements
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseRecyclerAdapter
import com.gym.o.gymoclock.functionality.workout_pr.recycler_items_swipe.setItemTouchHelper
import com.gym.o.gymoclock.functionality.workout_pr.rounds_picker.roundsPicker
import com.gym.o.gymoclock.functionality.common.workout_db_calls.addEditExercise
import com.gym.o.gymoclock.functionality.common.workout_db_calls.updateExerciseValues
import com.gym.o.gymoclock.interfaces.RecyclerViewInterface
import com.gym.o.gymoclock.utils.*


open class WorkoutFragment : DialogFragment(), RecyclerViewInterface {
    val TAG_NUMPICKER = "NumberPickerDialogs"

    private var _binding: FragmentWorkoutBinding? = null
    val binding get() = _binding!!

    private var isStartWorkout: Boolean = false
    private var isPauseWorkout: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

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

        binding.workoutWorkoutName.text = FormatUtils.prepareWorkoutTableStringDsSp(workoutTableName)

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
//            val position = dataList[iterator]
//
//            if (position.wTimerIsRunning) {
//                pauseTotalTimer()
//                listAdapter.pauseExerciseTimer(
//                    iterator, resources.getString(R.string.workout_stopped)
//                )
//            }
//
//            if (position.rTimerIsRunning) {
//                pauseTotalTimer()
//                listAdapter.pauseRestTimer(
//                    iterator,
//                    resources.getString(R.string.workout_stopped)
//                )
//            }
//        }
//
//        iterator = 0
        Log.i(
            "WorkFrag",
            "onDestroyView")
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

        dialogBuilderUtils.onClickListener(
            { updateExerciseValues(dataPosition) },
            { timePickerUtils.numberPickerTimeDialog(dialogBuilderUtils.workDigitalTime, dialogBuilderUtils.verifyButtonEditExercise) },
            { timePickerUtils.numberPickerTimeDialog(dialogBuilderUtils.restDigitalTime, dialogBuilderUtils.verifyButtonEditExercise) }
        )
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
                }, 500)
            dialogBuilderUtils.dialog.dismiss()
        }
        dialogBuilderUtils.cancelButtonRemoveExercise.setOnClickListener { dialogBuilderUtils.dialog.dismiss() }
    }


    override fun roundsCount() {
        rounds--
        recyclerPosition = 0
        val lastRecyclerPosition = dataList[listAdapter.itemCount - 1]
        binding.roundsPicker.value = rounds
        Log.d("MAIN", "Rounds Total: $rounds, isStartWorkout $isStartWorkout isPauseWorkout $isPauseWorkout -- ${DateTimeUtils.getCurrentTime()}")
        Log.d("MAIN", "iterator = $recyclerPosition rounds = $rounds -- ${DateTimeUtils.getCurrentTime()}")
        if (workoutTableName == "")
            return

        if (rounds == 0) {
            binding.roundsPicker.textColor = Color.RED
            //RoundsPickerFunctions.roundPickerColor(binding.roundsEdit, Color.RED)
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


    private lateinit var db: SQLiteDatabase
    lateinit var workCountDown: CountDownTimer
    lateinit var restCountDown: CountDownTimer

    override fun loadRecyclerViews() {
        workoutDB = WorkoutDB(requireActivity().applicationContext)
        db = workoutDB.readableDatabase
        dataList.clear()
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

        if(listAdapter.itemCount > 0)
            lastRestTimeCheck()

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong("prepareCountdownInMillis", prepareCountdownInMillis)
        outState.putBoolean("isPrepareCountdown", isPrepareCountdown)

        outState.putBoolean("isStartWorkout", isStartWorkout)
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
            isPrepareCountdown = savedInstanceState.getBoolean(
                "isPrepareCountdown",
                true)

            isStartWorkout = savedInstanceState.getBoolean("isStartWorkout")
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

    lateinit var listAdapter: ExerciseRecyclerAdapter
    lateinit var dialogBuilder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    lateinit var recyclerView: RecyclerView
    lateinit var dataList: ArrayList<ExerciseElements>
    lateinit var workoutDB: WorkoutDB
    lateinit var sharedPreferencesUtils: SharedPreferencesUtils

    private fun init() {
        sharedPreferencesUtils = SharedPreferencesUtils(requireContext())

        rounds = sharedPreferencesUtils.getRoundsValueFromPreferences()
        dataList = ArrayList()
        recyclerView = binding.recyclerView//findViewById(R.id.recycler_view)
        listAdapter = ExerciseRecyclerAdapter(requireContext(), this, dataList)

        recyclerView.apply {
            val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(getDrawable(context, R.drawable.divider_recycler_view)!!)
            addItemDecoration(dividerItemDecoration)
        }
        binding.swipeRefreshRecycler.setOnRefreshListener {
            if (!isStartWorkout) {
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
                Toast.makeText(context, "No workout available. Please add workout in order to register an exercise.", Toast.LENGTH_LONG).show()
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

            isStartWorkout = !isStartWorkout
            isPauseWorkout = !isPauseWorkout

            Log.d("isPauseWorkout", (isPauseWorkout).toString())
            Log.d("isStartWorkout", (isStartWorkout).toString())

            if (isStartWorkout) {

                if (isPrepareCountdown) {
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
            return@setOnClickListener
        }
    }


    private lateinit var calendarDB: CalendarDB

    private fun onEndOfWorkout() {
        binding.playPauseButton.background = getDrawable(requireContext(), R.drawable.ic_play_button)
        isStartWorkout = false
        isPauseWorkout = true
        isPrepareCountdown = true
        binding.roundsPicker.isEnabled = true
        calendarDB = CalendarDB(context)

        Log.d("CountDown", "rounds == 0 ${DateTimeUtils.getCurrentTime()}")

        val monthYear = "${DateTimeUtils.getCurrentMonth()} ${DateTimeUtils.getCurrentYear()}".replace(" ", "_")

        calendarDB.insertCalendarDetails(
            monthYear, DateTimeUtils.getDate(), startTime, DateTimeUtils.getCurrentTime(), workoutTableName,
            FormatUtils.convertTimeToDigitalClock(listAdapter.totalTimeFromDB(sharedPreferencesUtils.getRoundsValueFromPreferences()).toString()),
            FormatUtils.convertTimeToDigitalClock(listAdapter.totalWorkingTime(sharedPreferencesUtils.getRoundsValueFromPreferences()).toString())
        )
        startTime = ""
    }


    private fun addExerciseRecyclerView() {
        dialogBuilderUtils = DialogBuilderUtils(requireContext())
        timePickerUtils = TimePickerUtils(requireContext())

        dialogBuilderUtils.addOrEditExercise(false)

        dialogBuilderUtils.onClickListener(
            { addEditExercise() },
            { timePickerUtils.numberPickerTimeDialog(dialogBuilderUtils.workDigitalTime, dialogBuilderUtils.verifyButtonEditExercise) },
            { timePickerUtils.numberPickerTimeDialog(dialogBuilderUtils.restDigitalTime, dialogBuilderUtils.verifyButtonEditExercise) }
        )
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

}