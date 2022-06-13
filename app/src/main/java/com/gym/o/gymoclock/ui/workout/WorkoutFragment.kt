package com.gym.o.gymoclock.ui.workout

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Canvas
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
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.databinding.FragmentWorkoutBinding
import com.gym.o.gymoclock.functionality.calendar_pr.database.CalendarDB
import com.gym.o.gymoclock.functionality.workout_pr.*
import com.gym.o.gymoclock.functionality.workout_pr.countdown_functions.*
import com.gym.o.gymoclock.functionality.workout_pr.database.WorkoutDB
import com.gym.o.gymoclock.functionality.workout_pr.edit_workout.ConvertTime
import com.gym.o.gymoclock.functionality.workout_pr.edit_workout.WidgetsWarnings
import com.gym.o.gymoclock.functionality.workout_pr.rounds_picker.roundsPicker

import com.gym.o.gymoclock.functionality.workout_pr.user_adapter.Elements
import com.gym.o.gymoclock.functionality.workout_pr.user_adapter.UserAddActivityAdapter
import com.gym.o.gymoclock.interfaces.RecyclerViewInterface
import com.gym.o.gymoclock.utils.DateTimeUtils
import com.gym.o.gymoclock.utils.DialogBuilderUtils
import com.gym.o.gymoclock.utils.TimePickerUtils


open class WorkoutFragment : DialogFragment(), RecyclerViewInterface {
    val TAG_NUMPICKER = "NumberPickerDialogs"

    private var _binding: FragmentWorkoutBinding? = null
    val binding get() = _binding!!

    lateinit var listAdapter: UserAddActivityAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var dialogBuilder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    var isStartWorkout: Boolean = false
    private var isPauseWorkout: Boolean = true
    private lateinit var workoutDB: WorkoutDB
    private lateinit var db: SQLiteDatabase
    private lateinit var recyclerView: RecyclerView
    lateinit var dataList: ArrayList<Elements>

    private lateinit var workCountDown: CountDownTimer
    private lateinit var restCountDown: CountDownTimer

    private lateinit var dateTimeUtils: DateTimeUtils
    private lateinit var calendarDB: CalendarDB

    private lateinit var dialogBuilderUtils: DialogBuilderUtils
    private lateinit var timePickerUtils: TimePickerUtils

    private val addNewExercise = "add"
    private val updateExercise = "update"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init()
        onSavedInstance(savedInstanceState)
        if (workoutName != "") {
            loadRecyclerViews()
            binding.totalTime.text =
                ConvertTime.convertTimeToDigitalClock((listAdapter.totalTime(rounds)).toString())
        }

        setItemTouchHelper()

        recyclerView.smoothScrollToPosition(iterator)

        workoutInit()

        if (listAdapter.itemCount != 0)
            roundsPicker()

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
        Log.i("WorkFrag", "onDestroyView")
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

    private fun addExerciseRecyclerView() {
        dialogBuilderUtils = DialogBuilderUtils(requireContext())
        dialogBuilderUtils.dialogBuilderAddOrEditExercise()
        timePickerUtils = TimePickerUtils(requireContext())

        dialogBuilderUtils.onClickListener(
            { addEditExercise() },
            { timePickerUtils.numberPickerTimeDialog(dialogBuilderUtils.workDigitalTime) },
            { timePickerUtils.numberPickerTimeDialog(dialogBuilderUtils.restDigitalTime) }
        )
    }

    private fun addEditExercise() {
        var exerciseName = "Exercise Name"
        if (dialogBuilderUtils.exerciseNameEdit.text.toString().isNotEmpty())
            exerciseName = dialogBuilderUtils.exerciseNameEdit.text.toString().trim()

        if (nameIsNotDuplicate(exerciseName))
            WidgetsWarnings.editTextWarning(
                dialogBuilderUtils.exerciseNameEdit,
                "Exercise already registered"
            )
        else {
            val exerciseClock: TextView =
                dialogBuilderUtils.viewRecycler.findViewById(R.id.countdown_work)
            exerciseClock.text =
                dialogBuilderUtils.workDigitalTime.text.toString()//ConvertTime.convertTimeToDigitalClock(dialogBuilderExercise.workTimePicker.text.toString())

            val restClock: TextView =
                dialogBuilderUtils.viewRecycler.findViewById(R.id.countdown_rest)
            restClock.text =
                dialogBuilderUtils.restDigitalTime.text.toString()//ConvertTime.convertTimeToDigitalClock(dialogBuilderExercise.restTimePicker.text.toString())

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

            dataList.add(
                Elements(
                    exerciseName, exerciseClock, restClock, workCountDown, restCountDown,
                    wTimerIsRunning = false, wTimerIsPaused = false,
                    rTimerIsRunning = false, rTimerIsPaused = false
                )
            )
            saveExerciseValues(addNewExercise, "", exerciseName, exerciseClock, restClock)

            listAdapter.notifyDataSetChanged()

            binding.totalTime.text =
                ConvertTime.convertTimeToDigitalClock((listAdapter.totalTime(rounds)).toString())
            dialogBuilderUtils.dialog.dismiss()
        }
    }

    override fun editExercise(dataPosition: Int) {
        dialogBuilderUtils = DialogBuilderUtils(requireContext())
        dialogBuilderUtils.dialogBuilderAddOrEditExercise()
        timePickerUtils = TimePickerUtils(requireContext())

        dialogBuilderUtils.onClickListener(
            { updateExerciseValues(dataPosition) },
            { timePickerUtils.numberPickerTimeDialog(dialogBuilderUtils.workDigitalTime) },
            { timePickerUtils.numberPickerTimeDialog(dialogBuilderUtils.restDigitalTime) }
        )
    }

    private fun updateExerciseValues(dataPosition: Int) {
        val position = dataList[dataPosition]
        workoutDB = WorkoutDB(requireContext())

        if (nameIsNotDuplicate(dialogBuilderUtils.exerciseNameEdit.text.toString()))
            WidgetsWarnings.editTextWarning(
                dialogBuilderUtils.exerciseNameEdit,
                "Exercise already registered"
            )
    else {
        val oldExerciseName = position.exerciseNameValue

        if (dialogBuilderUtils.exerciseNameEdit.text.toString().isNotEmpty()) {
            position.exerciseNameValue = dialogBuilderUtils.exerciseNameEdit.text.toString()
            listAdapter.notifyItemChanged(dataPosition, position.exerciseNameValue)
        }

        if (dialogBuilderUtils.workDigitalTime.text.toString().isNotEmpty()) {
            position.exerciseClockValue.text =
                dialogBuilderUtils.workDigitalTime.text.toString()//ConvertTime.convertTimeToDigitalClock(dialogBuilderUtils.workTimePicker.text.toString())
            listAdapter.notifyItemChanged(dataPosition, position.exerciseClockValue)
        }

        if (dialogBuilderUtils.restDigitalTime.text.toString().isNotEmpty()) {
            position.restClockValue.text =
                dialogBuilderUtils.restDigitalTime.text.toString()//ConvertTime.convertTimeToDigitalClock(dialogBuilderUtils.restTimePicker.text.toString())
            listAdapter.notifyItemChanged(dataPosition, position.restClockValue)
        }

        saveExerciseValues(
            updateExercise, oldExerciseName, position.exerciseNameValue,
            position.exerciseClockValue, position.restClockValue
        )

        binding.totalTime.text =
            ConvertTime.convertTimeToDigitalClock((listAdapter.totalTime(rounds)).toString())

        if (listAdapter.totalTime(rounds) > 0) {
            binding.totalTimeTextView.setTextColor(
                getColor(
                    requireContext(),
                    R.color.custom_text_color
                )
            )
            binding.totalTime.setTextColor(
                getColor(
                    requireContext(),
                    R.color.custom_text_color
                )
            )
        }
        dialogBuilderUtils.dialog.dismiss()
        }
    }

    private fun saveExerciseValues(addOrUpdate: String, oldExerciseName: String, exerciseName: String, exerciseClock: TextView, restClock: TextView) {
        if (addOrUpdate == addNewExercise) {
            val insertExerciseData = workoutDB.insertExerciseDetails(
                workoutName,
                exerciseName,
                ConvertTime.convertTimeToSeconds(exerciseClock.text.toString()).toString(),
                ConvertTime.convertTimeToSeconds(restClock.text.toString()).toString()
            )
            if (insertExerciseData)
                Toast.makeText(context, "Exercise Inserted", Toast.LENGTH_SHORT).show()
        }

        if (addOrUpdate == updateExercise) {
            val updateExerciseData = workoutDB.updateExerciseDetails(
                workoutName, oldExerciseName, exerciseName,
                ConvertTime.convertTimeToSeconds(exerciseClock.text.toString())
                    .toString(),
                ConvertTime.convertTimeToSeconds(restClock.text.toString()).toString()
            )
            if (updateExerciseData)
                Toast.makeText(context, "Exercise Updated", Toast.LENGTH_SHORT).show()
        }
    }

    override fun removeExercise(itemView: View, dataPosition: Int) {
        workoutDB = WorkoutDB(requireContext())
        dialogBuilderUtils = DialogBuilderUtils(requireContext())
        val position = dataList[dataPosition]

        dialogBuilderUtils.dialogBuilderRemoveExercise()

        Log.e("WorkoutFrag", "PositionData: $dataPosition")

        dialogBuilderUtils.okButtonRemoveExercise.setOnClickListener {
            val deleteView = workoutDB.deleteWorkoutDetails(workoutName, position.exerciseNameValue)
            if (deleteView)
                Toast.makeText(context, "Exercise Deleted", Toast.LENGTH_SHORT).show()

            listAdapter.setOnRemoveViewAnimation(itemView, dataPosition)
            Handler(Looper.getMainLooper()).postDelayed({
                                                            dataList.removeAt(dataPosition)
                                                            listAdapter.notifyItemRemoved(
                                                                dataPosition
                                                            )
                                                            //listAdapter.notifyDataSetChanged()
                                                        }, 500)

            getLastPositionForAddViewAnimation = -1
            getLastPositionForRemoveViewAnimation = -1

            Log.e(
                "WorkoutFrag",
                "Item Position = $dataPosition, Animate Delete Position = $getLastPositionForRemoveViewAnimation" + " " +
                        "Animate Add Position = $getLastPositionForAddViewAnimation"
            )
            binding.totalTime.text =
                ConvertTime.convertTimeToDigitalClock((listAdapter.totalTime(rounds)).toString())
            dialogBuilderUtils.dialog.dismiss()
        }
        dialogBuilderUtils.cancelButtonRemoveExercise.setOnClickListener { dialog.dismiss() }
    }

    override fun roundsCount() {
        rounds--
        iterator = 0

        binding.roundsPicker.value = rounds
        Log.d(
            "MAIN",
            "Rounds Total: $rounds, isStartWorkout $isStartWorkout isPauseWorkout $isPauseWorkout -- ${DateTimeUtils.getCurrentTime()}"
        )
        Log.d(
            "MAIN",
            "iterator = $iterator rounds = $rounds -- ${DateTimeUtils.getCurrentTime()}"
        )
        if (workoutName == "")
            return

        if (listAdapter.totalTime(rounds) == 0) {
            binding.roundsPicker.textColor = Color.RED
            //RoundsPickerFunctions.roundPickerColor(binding.roundsEdit, Color.RED)
            binding.roundsPicker.value = 1
            onEndOfWorkout()
            return
        }

        if (rounds == 1) {
            dataList[listAdapter.itemCount - 1].restClockValue.text =
                ConvertTime.convertTimeToDigitalClock("0")
        }

        Log.d(
            "MAIN",
            "Start Timer Position = $iterator rounds = $rounds -- ${DateTimeUtils.getCurrentTime()}"
        )
        listAdapter.startExerciseTimer(iterator)
    }

    override fun scrollToPosition() {
        recyclerView.smoothScrollToPosition(iterator)
    }

    override fun loadRecyclerViews() {
        workoutDB = WorkoutDB(requireActivity().applicationContext)
        db = workoutDB.readableDatabase
        dataList.clear()
        val cursor: Cursor = workoutDB.loadRecyclerElements(workoutName, db)

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
                exerciseClock.text =
                    ConvertTime.convertTimeToDigitalClock(cursor.getInt(2).toString())
                restClock.text = ConvertTime.convertTimeToDigitalClock(cursor.getInt(3).toString())

                dataList.add(
                    Elements(
                        exerciseName.text.toString(), exerciseClock, restClock, workCountDown,
                        restCountDown, wTimerIsRunning = false, wTimerIsPaused = false,
                        rTimerIsRunning = false, rTimerIsPaused = false
                    )
                )

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        listAdapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong("prepareCountdownInMillis", prepareCountdownInMillis)
        outState.putBoolean("isPrepareCountdown", isPrepareCountdown)

        outState.putBoolean("isStartWorkout", isStartWorkout)
        outState.putInt("iterator", iterator)
        outState.putInt("rounds", rounds)

        outState.putLong("totalTimeInMillis", totalTimeInMillis)
        outState.putLong("workTimeInMillis", workTimeInMillis)
        outState.putLong("restTimeInMillis", restTimeInMillis)
        outState.putLong("endTime", endTime)
    }

    private fun onSavedInstance(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            prepareCountdownInMillis = savedInstanceState.getLong("prepareCountdownInMillis", 0)
            isPrepareCountdown = savedInstanceState.getBoolean("isPrepareCountdown", true)

            isStartWorkout = savedInstanceState.getBoolean("isStartWorkout")
            iterator = savedInstanceState.getInt("iterator")
            rounds = savedInstanceState.getInt("rounds")

            totalTimeInMillis = savedInstanceState.getLong("totalTimeInMillis")
            workTimeInMillis = savedInstanceState.getLong("workTimeInMillis")
            restTimeInMillis = savedInstanceState.getLong("restTimeInMillis")

            if (dataList[iterator].wTimerIsRunning) {
                endTime = savedInstanceState.getLong("endTime")
                totalTimeInMillis = endTimeTotalTimer - System.currentTimeMillis()
                workTimeInMillis = endTime - System.currentTimeMillis()
                startTotalTimer()
                listAdapter.startExerciseTimer(iterator)
            }

            if (dataList[iterator].rTimerIsRunning) {
                restTimeInMillis = endTime - System.currentTimeMillis()
                startTotalTimer()
                listAdapter.startRestTimer(iterator)

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

    private fun init() {
        sharedPreferences = requireContext().getSharedPreferences("Rounds", Context.MODE_PRIVATE)
        rounds = sharedPreferences.getInt("roundsInt", -1)
        dataList = ArrayList()
        recyclerView = binding.recyclerView//findViewById(R.id.recycler_view)
        listAdapter = UserAddActivityAdapter(requireContext(), this, dataList)
        workoutDB = WorkoutDB(requireActivity().applicationContext)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        recyclerView.adapter = listAdapter
    }

    private fun workoutInit() {
        binding.addLayout.setOnClickListener {
            if (workoutName.isEmpty()) {
                Toast.makeText(
                    context,
                    "No workout available. Please add workout in order to register an exercise.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            addExerciseRecyclerView()
            return@setOnClickListener
        }

        binding.playPauseButton.setOnClickListener {
            if (rounds == 0) {
                iterator = 0
                WidgetsWarnings.pickerTextWarning(binding.roundsPicker, rounds)
                return@setOnClickListener
            }

            if (listAdapter.itemCount == 0 || listAdapter.totalTime(rounds) == 0) {
                binding.totalTimeTextView.setTextColor(Color.RED)
                WidgetsWarnings.textViewWarning(binding.totalTime)
                return@setOnClickListener
            }

            isStartWorkout = !isStartWorkout
            isPauseWorkout = !isPauseWorkout
            Log.d("isPauseWorkout", (isPauseWorkout).toString())

            if (isStartWorkout) {

                if (isPrepareCountdown) {
                    prepareForWorkoutTimer()
                    return@setOnClickListener
                }

                binding.playPauseButton.background =
                    getDrawable(requireContext(), R.drawable.ic_pause_button)
                startTotalTimer()
                listAdapter.startExerciseTimer(iterator)

                return@setOnClickListener
            }

            val position = dataList[iterator]
            if (position.wTimerIsRunning) listAdapter.pauseExerciseTimer(
                iterator,
                resources.getString(R.string.exercise_paused)
            )

            if (position.rTimerIsRunning) listAdapter.pauseRestTimer(
                iterator,
                resources.getString(R.string.rest_paused)
            )

            pauseTotalTimer()
            binding.playPauseButton.background =
                getDrawable(requireContext(), R.drawable.ic_play_button)
            return@setOnClickListener
        }
    }

    private fun onEndOfWorkout() {
        binding.playPauseButton.background =
            getDrawable(requireContext(), R.drawable.ic_play_button)
        isStartWorkout = false
        isPauseWorkout = true
        isPrepareCountdown = true

        calendarDB = CalendarDB(context)
        this.dateTimeUtils = DateTimeUtils()
        Log.d("CountDown", "rounds == 0 ${DateTimeUtils.getCurrentTime()}")

        val monthYear =
            "${DateTimeUtils.getCurrentMonth()} ${DateTimeUtils.getCurrentYear()}".replace(" ", "_")

        calendarDB.insertCalendarDetails(
            monthYear,
            DateTimeUtils.getDate(),
            startTime,
            DateTimeUtils.getCurrentTime(),
            workoutName,
            ConvertTime.convertTimeToDigitalClock(
                listAdapter.totalTime(
                    sharedPreferences.getInt(
                        "roundsInt",
                        -1
                    )
                ).toString()
            ),
            ConvertTime.convertTimeToDigitalClock(
                listAdapter.totalWorkingTime(
                    sharedPreferences.getInt(
                        "roundsInt",
                        -1
                    )
                ).toString()
            )
        )
        startTime = ""
        isPrepareCountdown = true
    }

    private fun nameIsNotDuplicate(name: String): Boolean {
        workoutDB = WorkoutDB(requireActivity().applicationContext)
        val nameTemp: List<String> = workoutDB.checkForDuplicateNames(workoutName)

        for (temp in nameTemp)
            if (name == temp)
                return true

        return false
    }

    private fun dipToPx(dipValue: Float, context: Context): Int {
        return (dipValue * context.resources.displayMetrics.density).toInt()
    }

    private fun setItemTouchHelper() {
        ItemTouchHelper(object : ItemTouchHelper.Callback() {

            //limit of swipe
            private val limitScroll = dipToPx(60f, requireActivity())
            private var currentScrollX = 0
            private var currentScrollXWhenActive = 0
            private var initXWhenInActive = 0f
            private var firstInActive = false

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val drag = 0
                val swipe = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(drag, swipe)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return Integer.MAX_VALUE.toFloat()
            }

            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                return Integer.MAX_VALUE.toFloat()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dX == 0f) {
                        currentScrollX = viewHolder.itemView.scrollX
                        firstInActive = true
                    }

                    if (isCurrentlyActive) {
                        var scrollOffSet = currentScrollX + (-dX).toInt()

                        if (scrollOffSet > limitScroll) {
                            scrollOffSet = limitScroll
                        } else if (scrollOffSet < 0) {
                            scrollOffSet = 0
                        }
                        viewHolder.itemView.scrollTo(scrollOffSet, 0)
                    } else {

                        if (firstInActive) {
                            firstInActive = false
                            currentScrollXWhenActive = viewHolder.itemView.scrollX
                            initXWhenInActive = dX
                        }

                        if (viewHolder.itemView.scrollX < limitScroll) {
                            viewHolder.itemView.scrollTo(
                                (currentScrollXWhenActive * dX / initXWhenInActive).toInt(),
                                0
                            )
                        }
                    }
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                if (viewHolder.itemView.scrollX > limitScroll) {
                    viewHolder.itemView.scrollTo(limitScroll, 0)
                } else if (viewHolder.itemView.scrollX < 0) {
                    viewHolder.itemView.scrollTo(0, 0)
                }
            }
        }).apply { attachToRecyclerView(recyclerView) }
    }

}