package com.gym.o.gymoclock.ui.workout

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
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
import com.gym.o.gymoclock.functionality.workout_pr.database.WorkoutDB
import com.gym.o.gymoclock.functionality.workout_pr.edit_workout.*
import com.gym.o.gymoclock.functionality.workout_pr.user_adapter.Elements
import com.gym.o.gymoclock.functionality.workout_pr.user_adapter.UserAddActivityAdapter
import com.gym.o.gymoclock.interfaces.RecyclerViewInterface
import com.gym.o.gymoclock.utils.DateTimeUtils
import com.gym.o.gymoclock.utils.TextToSpeechUtils
import java.lang.reflect.Field
import java.util.*
import kotlin.properties.Delegates


open class WorkoutFragment : DialogFragment(), RecyclerViewInterface {
    val TAG_NUMPICKER = "NumberPicker"

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var listAdapter: UserAddActivityAdapter
    private lateinit var workoutDB: WorkoutDB
    private lateinit var db: SQLiteDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<Elements>

    private lateinit var dialogBuilder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog

    private lateinit var workCountDown: CountDownTimer
    private lateinit var restCountDown: CountDownTimer

    private var isStartWorkout: Boolean = false
    private var isPauseWorkout: Boolean = true
    private lateinit var totalTimer: CountDownTimer
    private var totalTimeInMillis by Delegates.notNull<Long>()
    private var timeDifference by Delegates.notNull<Int>()

    private lateinit var dateTimeUtils: DateTimeUtils
    private lateinit var calendarDB: CalendarDB
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireContext().getSharedPreferences("Rounds", Context.MODE_PRIVATE)
        rounds = sharedPreferences.getInt("roundsInt", -1)

        init()
        if (workoutName != "") {
            loadRecyclerViews()
            binding.totalTime.text =
                convertTimeToDigitalClock((listAdapter.totalTime(rounds)).toString())
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
        TextToSpeechUtils.getInstance(requireContext()).stopTTS()
        binding.addLayout.isEnabled = false
        binding.addLayout.isClickable = false

        binding.playPauseButton.isEnabled = false
        binding.playPauseButton.isClickable = false

        binding.roundsEdit.isEnabled = false
        binding.roundsEdit.isClickable = false

        val position = dataList[iterator]

        if (position.wTimerIsRunning) {
            pauseTotalTimer()
            listAdapter.pauseExerciseTimer(iterator, resources.getString(R.string.workout_stopped))
            //position.wCountDownTimer.cancel()
        }
        if (position.rTimerIsRunning) {
            pauseTotalTimer()
            listAdapter.pauseRestTimer(iterator, resources.getString(R.string.workout_stopped))
            //position.rCountDownTimer.cancel()
        }

        Log.i("WorkFrag", "onDestroyView")
        super.onDestroyView()

    }

    override fun onDestroy() {
        super.onDestroy()
        TextToSpeechUtils.getInstance(requireContext()).stopTTS()
        binding.addLayout.isEnabled = false
        binding.addLayout.isClickable = false

        binding.playPauseButton.isEnabled = false
        binding.playPauseButton.isClickable = false

        binding.roundsEdit.isEnabled = false
        binding.roundsEdit.isClickable = false

        _binding = null
        Log.i("WorkFrag", "onDestroy")

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
                    convertTimeToDigitalClock(workTimeEdit.text.toString())
                listAdapter.notifyItemChanged(dataPosition, position.exerciseClockValue)
            }

            if (restTimeEdit.text.toString() != "") {
                position.restClockValue.text =
                    convertTimeToDigitalClock(restTimeEdit.text.toString())
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
                    convertTimeToSeconds(position.exerciseClockValue.text.toString()).toString(),
                    convertTimeToSeconds(position.restClockValue.text.toString()).toString()
                )
                if (updateExerciseData)
                    Toast.makeText(context, "Exercise Updated", Toast.LENGTH_SHORT).show()
            }
            binding.totalTime.text =
                convertTimeToDigitalClock((listAdapter.totalTime(rounds)).toString())

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
                convertTimeToDigitalClock((listAdapter.totalTime(rounds)).toString())
            dialog.dismiss()
        }

        cancelButton.setOnClickListener { dialog.dismiss() }
    }

    override fun roundsCount() {
        val dateTimeUtils = DateTimeUtils()
        rounds--
        iterator = 0
        //if (rounds < 1) rounds = 1
        binding.roundsEdit.value = rounds
        Log.d(
            "MAIN",
            "Rounds Total: $rounds, isStartWorkout $isStartWorkout isPauseWorkout $isPauseWorkout -- ${dateTimeUtils.getCurrentTime()}"
        )
        Log.d("MAIN", "iterator = $iterator rounds = $rounds -- ${dateTimeUtils.getCurrentTime()}")
        if (workoutName == "")
            return

        if (listAdapter.totalTime(rounds) == 0) {
            //binding.roundsEdit.textColor = Color.RED
            roundPickerColor(binding.roundsEdit, Color.RED)
            binding.roundsEdit.value = 1
            onEndOfWorkout()
            return
        }

        Log.d(
            "MAIN",
            "Start Timer Position = $iterator rounds = $rounds -- ${dateTimeUtils.getCurrentTime()}"
        )
        listAdapter.startExerciseTimer(iterator)
    }

    private fun onEndOfWorkout() {
        val dateTimeUtils = DateTimeUtils()
        binding.playPauseButton.background =
            getDrawable(requireContext(), R.drawable.ic_play_button)
        isStartWorkout = false
        isPauseWorkout = true

        calendarDB = CalendarDB(context)
        this.dateTimeUtils = DateTimeUtils()
        Log.d("CountDown", "rounds == 0 ${dateTimeUtils.getCurrentTime()}")

        val monthYear =
            "${dateTimeUtils.getCurrentMonth()} ${dateTimeUtils.getCurrentYear()}".replace(" ", "_")
        //monthYearTable: String, date: String, workoutName:  totalTime: String, totalWorkingTime: String
        calendarDB.insertCalendarDetails(
            monthYear,
            dateTimeUtils.getDate(),
            startTime,
            dateTimeUtils.getCurrentTime(),
            workoutName,
            convertTimeToDigitalClock(listAdapter.totalTime(5).toString()),
            convertTimeToDigitalClock(listAdapter.totalWorkingTime(5).toString())
        )
        startTime = ""
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
                        restTimeInMillis = millsUntilFinish
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
                exerciseClock.text = convertTimeToDigitalClock(cursor.getInt(2).toString())
                restClock.text = convertTimeToDigitalClock(cursor.getInt(3).toString())

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

    override fun scrollToPosition() {
        recyclerView.smoothScrollToPosition(iterator)
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
                exerciseClock.text = convertTimeToDigitalClock(workTimeEdit.text.toString())

                val restClock: TextView = viewRecycler.findViewById(R.id.countdown_rest)
                restClock.text = convertTimeToDigitalClock(restTimeEdit.text.toString())

                workCountDown = object : CountDownTimer(0, 1000) {
                    override fun onTick(millsUntilFinish: Long) {
                        restTimeInMillis = millsUntilFinish
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
                    convertTimeToDigitalClock((listAdapter.totalTime(rounds)).toString())
                dialog.dismiss()
                return@setOnClickListener
            }

            Log.w("Add Exercise", "Past no Database")

            if (nameIsDuplicate(exerciseName))
                editTextWarning(exerciseNameEdit, "Exercise already registered")
            else {
                val exerciseClock: TextView = viewRecycler.findViewById(R.id.countdown_work)
                exerciseClock.text = convertTimeToDigitalClock(workTimeEdit.text.toString())

                val restClock: TextView = viewRecycler.findViewById(R.id.countdown_rest)
                restClock.text = convertTimeToDigitalClock(restTimeEdit.text.toString())

                workCountDown = object : CountDownTimer(0, 1000) {
                    override fun onTick(millsUntilFinish: Long) {
                        restTimeInMillis = millsUntilFinish
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
                    convertTimeToDigitalClock((listAdapter.totalTime(rounds)).toString())
                dialog.dismiss()
            }

        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun dipToPx(dipValue: Float, context: Context): Int {
        return (dipValue * context.resources.displayMetrics.density).toInt()
    }

    private fun init() {

        dataList = ArrayList()
        recyclerView = binding.recyclerView//findViewById(R.id.recycler_view)
        listAdapter = UserAddActivityAdapter(requireContext(), this, dataList)
        workoutDB = WorkoutDB(requireActivity().applicationContext)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        recyclerView.adapter = listAdapter

    }

    private fun nameIsDuplicate(name: String): Boolean {

        workoutDB = WorkoutDB(requireActivity().applicationContext)
        val nameTemp: List<String> = workoutDB.checkForDuplicateNames(workoutName)

        for (temp in nameTemp)
            if (name == temp)
                return true

        return false

    }

    private fun refreshTotalTimeOnRoundsChanged(timeDifference: Int): Int {
        return (listAdapter.totalTime(rounds)) - timeDifference
    }

    private fun roundsPicker() {

        binding.roundsEdit.minValue = 1
        binding.roundsEdit.maxValue = 20
        binding.roundsEdit.value = rounds

        Log.d(TAG_NUMPICKER, "${binding.roundsEdit.value}")

        binding.roundsEdit.setOnScrollListener { view, scrollState ->
            when (scrollState) {
                NumberPicker.OnScrollListener.SCROLL_STATE_IDLE -> onScrollIdle(view)
                NumberPicker.OnScrollListener.SCROLL_STATE_FLING -> onScrollFlying()
                NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL -> onScrollTouched(view)
            }
        }

    }

    private fun onScrollIdle(scrollView: NumberPicker) {

        Log.i(TAG_NUMPICKER, "Scroll Idle")

        var oldValue = rounds
        Log.d(TAG_NUMPICKER, "OldValue= $oldValue")

        rounds = scrollView.value
        saveRoundsValueToPreferences(scrollView)
        Log.d(TAG_NUMPICKER, "Value rounds = $rounds")

        timeDifference = timeDifference(listAdapter.totalTime(oldValue))
        binding.totalTime.text =
            convertTimeToDigitalClock(refreshTotalTimeOnRoundsChanged(timeDifference).toString())

        if (isStartWorkout) {
            pauseTotalTimer()
            startTotalTimer()
        }

        if (scrollView.value == 0) roundPickerColor(binding.roundsEdit, Color.RED)//binding.roundsEdit.textColor = Color.RED
        else roundPickerColor(binding.roundsEdit, getColor(
            requireContext(),
            R.color.number_picker_scroll_idle))
//        binding.roundsEdit.textColor = getColor(
//            requireContext(),
//            R.color.number_picker_scroll_idle
//        )

    }

    private fun onScrollFlying() {
        Log.i("NumberPicker", "Scroll Flying")
        roundPickerColor(binding.roundsEdit, getColor(
            requireContext(),
            R.color.number_picker_scroll_flying))
//        binding.roundsEdit.textColor = getColor(
//            requireContext(),
//            R.color.number_picker_scroll_flying
//        )
    }

    private fun onScrollTouched(scrollView: NumberPicker) {
        Log.i(TAG_NUMPICKER, "Scroll Touch Scroll")
        roundPickerColor(binding.roundsEdit, getColor(
            requireContext(),
            R.color.number_picker_scroll_touched))
//        binding.roundsEdit.textColor = getColor(
//            requireContext(),
//            R.color.number_picker_scroll_touched
//        )
    }

    private fun saveRoundsValueToPreferences(scrollView: NumberPicker) {
        val save = sharedPreferences.edit()
        save.putInt("roundsInt", scrollView.value)
        save.apply()
        Log.i(TAG_NUMPICKER, "Round Value Saved: New Value = ${scrollView.value}")
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

    private fun timeDifference(totalTime: Int): Int {
        val currentTotalTime: Int = convertTimeToSeconds(binding.totalTime.text.toString()).toInt()
        return totalTime - currentTotalTime
    }

    private fun startTotalTimer() {

        totalTimeInMillis = convertTimeToMillis(binding.totalTime.text.toString())

        totalTimer = object : CountDownTimer(totalTimeInMillis, 1000) {
            override fun onTick(millsUntilFinish: Long) {
                totalTimeInMillis = millsUntilFinish

                updateTotalTimerUI()
            }

            override fun onFinish() {
            }
        }.start()
    }

    private fun pauseTotalTimer() {
        totalTimer.cancel()
    }

    private fun updateTotalTimerUI() {
        val minutesWork = (totalTimeInMillis / 1000) / 60
        val secondsWork = (totalTimeInMillis / 1000) % 60
        val totalCount: String =
            String.format(Locale.getDefault(), "%02d:%02d", minutesWork, secondsWork)
        binding.totalTime.text = totalCount
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

    private fun roundPickerColor(numberPicker: NumberPicker, color: Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            numberPicker.textColor = color
        }
        else {
            val count = numberPicker.childCount
            for (i in 0 until count) {
                val child = numberPicker.getChildAt(i)
                if (child is EditText) {
                    try {
                        child.setTextColor(color)
                        numberPicker.invalidate()
                        val fieldSelectorWheelPaint: Field =
                            numberPicker.javaClass.getDeclaredField("mSelectorWheelPaint")
                        var isAccessible: Boolean = fieldSelectorWheelPaint.isAccessible
                        fieldSelectorWheelPaint.isAccessible = true
                        val paint: Paint = fieldSelectorWheelPaint.get(numberPicker) as Paint
                        if (paint != null) {
                            paint.color = color
                            fieldSelectorWheelPaint.isAccessible = isAccessible
                            numberPicker.invalidate()
                        }
                        val fieldSelectionDivider: Field =
                            numberPicker.javaClass.getDeclaredField("mSelectorWheelPaint")
                        isAccessible = fieldSelectionDivider.isAccessible
                        fieldSelectionDivider.isAccessible = true
                        fieldSelectionDivider.set(numberPicker, null)
                        fieldSelectionDivider.isAccessible = isAccessible
                        numberPicker.invalidate()
                    } catch (ex: Exception) {
                        Log.e("NumberPickerColor", "Field Selection Exception")
                    }
                }
            }
        }
   }

}