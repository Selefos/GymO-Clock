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
import android.widget.Button
import android.widget.EditText
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
import com.gym.o.gymoclock.functionality.workout_pr.edit_workout.*
import com.gym.o.gymoclock.functionality.workout_pr.edit_workout.WidgetsWarnings.editTextWarning
import com.gym.o.gymoclock.functionality.workout_pr.edit_workout.WidgetsWarnings.pickerTextWarning
import com.gym.o.gymoclock.functionality.workout_pr.edit_workout.WidgetsWarnings.textViewWarning
import com.gym.o.gymoclock.functionality.workout_pr.rounds_picker.roundsPicker
import com.gym.o.gymoclock.functionality.workout_pr.user_adapter.Elements
import com.gym.o.gymoclock.functionality.workout_pr.user_adapter.UserAddActivityAdapter
import com.gym.o.gymoclock.interfaces.RecyclerViewInterface
import com.gym.o.gymoclock.utils.DateTimeUtils


open class WorkoutFragment : DialogFragment(), RecyclerViewInterface {
    val TAG_NUMPICKER = "NumberPicker"

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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

    override fun editExercise(dataPosition: Int) {
        dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
        workoutDB = WorkoutDB(requireContext())
        val position = dataList[dataPosition]
        val inflater = layoutInflater//LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.edit_workout, null)
        val exerciseNameEdit = view.findViewById<EditText>(R.id.exercise_name_edit)
        val workTimeEdit = view.findViewById<EditText>(R.id.work_time_edit)
        val restTimeEdit = view.findViewById<EditText>(R.id.rest_time_edit)
        val okButton = view.findViewById<Button>(R.id.ok_button)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)

        dialogBuilder.setView(view)
        dialog = dialogBuilder.create()
        dialog.show()

        okButton.setOnClickListener {
            val oldExerciseName = position.exerciseNameValue

            if (exerciseNameEdit.text.toString() != "") {
                position.exerciseNameValue = exerciseNameEdit.text.toString()
                listAdapter.notifyItemChanged(dataPosition, position.exerciseNameValue)
            }

            if (workTimeEdit.text.toString() != "") {
                position.exerciseClockValue.text =
                    ConvertTime.convertTimeToDigitalClock(workTimeEdit.text.toString())
                listAdapter.notifyItemChanged(dataPosition, position.exerciseClockValue)
            }

            if (restTimeEdit.text.toString() != "") {
                position.restClockValue.text =
                    ConvertTime.convertTimeToDigitalClock(restTimeEdit.text.toString())
                listAdapter.notifyItemChanged(dataPosition, position.restClockValue)
            }

            position.wTimerIsRunning = false
            listAdapter.notifyItemChanged(dataPosition, position.wTimerIsRunning)

            position.wTimerIsPaused = false
            listAdapter.notifyItemChanged(dataPosition, position.wTimerIsPaused)

            position.rTimerIsRunning = false
            listAdapter.notifyItemChanged(dataPosition, position.rTimerIsRunning)

            position.rTimerIsPaused = false
            listAdapter.notifyItemChanged(dataPosition, position.rTimerIsPaused)

            //notifyDataSetChanged()

            if (workoutName != "") {
                val updateExerciseData = workoutDB.updateExerciseDetails(
                    workoutName, oldExerciseName, position.exerciseNameValue,
                    ConvertTime.convertTimeToSeconds(position.exerciseClockValue.text.toString()).toString(),
                    ConvertTime.convertTimeToSeconds(position.restClockValue.text.toString()).toString()
                )
                if (updateExerciseData)
                    Toast.makeText(context, "Exercise Updated", Toast.LENGTH_SHORT).show()
            }
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

            dialog.dismiss()
        }

        cancelButton.setOnClickListener { dialog.dismiss() }
    }

    override fun removeExercise(itemView: View, dataPosition: Int) {
        dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
        workoutDB = WorkoutDB(context)
        val position = dataList[dataPosition]
        val inflater = layoutInflater//LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.delete_exercise, null)
        val okButton = view.findViewById<Button>(R.id.verify_exercise_delete)
        val cancelButton = view.findViewById<Button>(R.id.cancel_exercise_delete)

        dialogBuilder.setView(view)
        dialog = dialogBuilder.create()
        dialog.show()

        Log.e("WorkoutFrag", "PositionData: $dataPosition")

        okButton.setOnClickListener {

            if (workoutName != "") {
                val deleteView =
                    workoutDB.deleteWorkoutDetails(workoutName, position.exerciseNameValue)
                if (deleteView)
                    Toast.makeText(context, "Exercise Deleted", Toast.LENGTH_SHORT).show()
            }

            listAdapter.setOnRemoveViewAnimation(itemView, dataPosition)
            Handler(Looper.getMainLooper()).postDelayed({
                dataList.removeAt(dataPosition)
                listAdapter.notifyItemRemoved(dataPosition)
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
            dialog.dismiss()
        }

        cancelButton.setOnClickListener { dialog.dismiss() }
    }

    override fun roundsCount() {
        rounds--
        iterator = 0

        binding.roundsEdit.value = rounds
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
            binding.roundsEdit.textColor = Color.RED
            //RoundsPickerFunctions.roundPickerColor(binding.roundsEdit, Color.RED)
            binding.roundsEdit.value = 1
            onEndOfWorkout()
            return
        }

        if(rounds == 1){
            dataList[listAdapter.itemCount - 1].restClockValue.text = ConvertTime.convertTimeToDigitalClock("0")
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

                val inflaterRecycler =
                    layoutInflater //LayoutInflater.from(requireActivity().applicationContext)
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
                exerciseClock.text = ConvertTime.convertTimeToDigitalClock(cursor.getInt(2).toString())
                restClock.text = ConvertTime.convertTimeToDigitalClock(cursor.getInt(3).toString())

                dataList.add(
                    Elements(
                        exerciseName.text.toString(),
                        exerciseClock,
                        restClock,
                        workCountDown,
                        restCountDown,
                        wTimerIsRunning = false,
                        wTimerIsPaused = false,
                        rTimerIsRunning = false,
                        rTimerIsPaused = false
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
        outState.putLong("workTimeInMillis" ,workTimeInMillis)
        outState.putLong("restTimeInMillis", restTimeInMillis)
        outState.putLong("endTime", endTime)
    }

    private fun onSavedInstance(savedInstanceState: Bundle?){
        if (savedInstanceState != null) {
            prepareCountdownInMillis = savedInstanceState.getLong("prepareCountdownInMillis", 0)
            isPrepareCountdown = savedInstanceState.getBoolean("isPrepareCountdown", true)

            isStartWorkout = savedInstanceState.getBoolean("isStartWorkout")
            iterator = savedInstanceState.getInt("iterator")
            rounds = savedInstanceState.getInt("rounds")

            totalTimeInMillis = savedInstanceState.getLong("totalTimeInMillis")
            workTimeInMillis = savedInstanceState.getLong("workTimeInMillis")
            restTimeInMillis = savedInstanceState.getLong("restTimeInMillis")

            if(dataList[iterator].wTimerIsRunning) {
                endTime = savedInstanceState.getLong("endTime")
                totalTimeInMillis = endTimeTotalTimer - System.currentTimeMillis()
                workTimeInMillis = endTime - System.currentTimeMillis()
                startTotalTimer()
                listAdapter.startExerciseTimer(iterator)
            }

            if(dataList[iterator].rTimerIsRunning){
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

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        if (savedInstanceState != null) {
//            prepareCountdownInMillis = savedInstanceState.getLong("prepareCountdownInMillis", 0)
//            isPrepareCountdown = savedInstanceState.getBoolean("isPrepareCountdown")
//        }
//    }

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
            addExercise()
            return@setOnClickListener
        }

        binding.playPauseButton.setOnClickListener {

            if (rounds == 0) {
                iterator = 0
                pickerTextWarning(binding.roundsEdit, rounds)
                return@setOnClickListener
            }

            if (listAdapter.itemCount == 0 || listAdapter.totalTime(rounds) == 0) {
                binding.totalTimeTextView.setTextColor(Color.RED)
                textViewWarning(binding.totalTime)
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

    private fun addExercise() {

        dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
        val inflater = layoutInflater //LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.edit_workout, null)
        val inflaterRecycler = layoutInflater //LayoutInflater.from(requireContext())
        val viewRecycler = inflaterRecycler.inflate(R.layout.add_view, null)
        val exerciseNameEdit = view.findViewById<EditText>(R.id.exercise_name_edit)
        val workTimeEdit = view.findViewById<EditText>(R.id.work_time_edit)
        val restTimeEdit = view.findViewById<EditText>(R.id.rest_time_edit)
        val okButton = view.findViewById<Button>(R.id.ok_button)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)


        dialogBuilder.setView(view)
        dialog = dialogBuilder.create()
        dialog.show()

        okButton.setOnClickListener {

            Log.w("Add Exercise", workoutName)
            var exerciseName = "Exercise Name"
            if (exerciseNameEdit.text.toString().isNotEmpty())
                exerciseName = exerciseNameEdit.text.toString().trim()

            if (workoutName.isEmpty() || workoutName == "temp") {
                val exerciseClock: TextView = viewRecycler.findViewById(R.id.countdown_work)
                exerciseClock.text = ConvertTime.convertTimeToDigitalClock(workTimeEdit.text.toString())

                val restClock: TextView = viewRecycler.findViewById(R.id.countdown_rest)
                restClock.text = ConvertTime.convertTimeToDigitalClock(restTimeEdit.text.toString())

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
                listAdapter.notifyDataSetChanged()

//                val insertExerciseData = workoutDB.insertExerciseDetails(
//                    workoutName, exerciseName, workTimeEdit.text.toString(),
//                    restTimeEdit.text.toString()
//                )
//                if (insertExerciseData)
//                    Toast.makeText(context, "Exercise Added", Toast.LENGTH_SHORT).show()

                binding.totalTime.text =
                    ConvertTime.convertTimeToDigitalClock((listAdapter.totalTime(rounds)).toString())
                dialog.dismiss()
                return@setOnClickListener
            }

            Log.w("Add Exercise", "Past no Database")

            if (nameIsDuplicate(exerciseName))
                editTextWarning(exerciseNameEdit, "Exercise already registered")
            else {
                val exerciseClock: TextView = viewRecycler.findViewById(R.id.countdown_work)
                exerciseClock.text = ConvertTime.convertTimeToDigitalClock(workTimeEdit.text.toString())

                val restClock: TextView = viewRecycler.findViewById(R.id.countdown_rest)
                restClock.text = ConvertTime.convertTimeToDigitalClock(restTimeEdit.text.toString())

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
                listAdapter.notifyDataSetChanged()

                val insertExerciseData = workoutDB.insertExerciseDetails(
                    workoutName, exerciseName, workTimeEdit.text.toString(),
                    (restTimeEdit.text.toString())
                )
                if (insertExerciseData)
                    Toast.makeText(context, "Exercise Inserted", Toast.LENGTH_SHORT).show()

                binding.totalTime.text =
                    ConvertTime.convertTimeToDigitalClock((listAdapter.totalTime(rounds)).toString())
                dialog.dismiss()
            }

        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun nameIsDuplicate(name: String): Boolean {

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