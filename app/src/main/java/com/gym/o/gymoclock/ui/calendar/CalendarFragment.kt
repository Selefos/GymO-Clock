package com.gym.o.gymoclock.ui.calendar

import android.app.DatePickerDialog.OnDateSetListener
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.databases.CalendarDB
import com.gym.o.gymoclock.databinding.FragmentCalendarBinding
import com.gym.o.gymoclock.utils.ConvertDigitalClocksUtils
import com.gym.o.gymoclock.utils.DateTimeUtils
import com.gym.o.gymoclock.utils.DialogBuilderUtils
import java.time.Month


class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val mDisplayDate: TextView? = null
    private var mDateSetListener: OnDateSetListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val calendarViewModel =
            ViewModelProvider(this)[CalendarViewModel::class.java]

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textCalendar
        calendarViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it
        }

        onDateChangeListener()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun onDateChangeListener() {

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val month = month + 1
            Log.d("TAG", "onDateSet: mm/dd/yyy: $dayOfMonth-$month-$year ${DateTimeUtils.setCalendarTableName()}")

            val date = String.format("%02d-%02d-%02d", dayOfMonth, month, year)
            val tableName = "${Month.of(month)}_$year"
            Log.i("Date Format", date)

            binding.buttonPopLayout.removeAllViews()
            showWorkoutDetails(tableName, date)
        }

    }

    private fun showWorkoutDetails(tableName: String, date: String) {

        val calendarDB = CalendarDB(context)
        val sqlDB: SQLiteDatabase = calendarDB.readableDatabase
        val cursor: Cursor = calendarDB.getCalendarWorkoutID(tableName, date, sqlDB)

        if (cursor.moveToFirst()) {
            do {
                addButton(cursor.getString(0), cursor.getString(1).replace("_", " "), tableName)
            } while (cursor.moveToNext())
        }
        cursor.close()
        sqlDB.close()

    }

    private fun addButton(workoutID: String, workoutText: String, tableName: String) {

        val button = Button(context)
        button.background = AppCompatResources.getDrawable(requireContext(), R.drawable.buttons_background)
        button.text = workoutText
        button.setTextColor(Color.WHITE)

        button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        binding.buttonPopLayout.addView(button)

        button.setOnClickListener {

            val calendarDB = CalendarDB(context)
            val sqlDB: SQLiteDatabase = calendarDB.readableDatabase
            val cursor: Cursor = calendarDB.loadCalendarDetails(tableName, workoutID, sqlDB)
            val dialogBuilderUtils = DialogBuilderUtils(requireContext())

            if (cursor.moveToFirst()) {
                do {
                    dialogBuilderUtils.calendarDate.text = cursor.getString(0)
                    dialogBuilderUtils.calendarStartTime.text = cursor.getString(1)
                    dialogBuilderUtils.calendarEndTime.text = cursor.getString(2)
                    dialogBuilderUtils.calendarWorkoutName.text = cursor.getString(3).replace("_", " ")
                    dialogBuilderUtils.calendarTotalTime.text = ConvertDigitalClocksUtils.convertTimeMinSecFormat(cursor.getString(4))
                    dialogBuilderUtils.calendarTotalWorkingTime.text = ConvertDigitalClocksUtils.convertTimeMinSecFormat(cursor.getString(5))
                } while (cursor.moveToNext())
            }
            cursor.close()
            sqlDB.close()

            dialogBuilderUtils.calendarWorkoutDetails(false)

        }

    }

}