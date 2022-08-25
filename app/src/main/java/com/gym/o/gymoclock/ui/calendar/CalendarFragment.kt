package com.gym.o.gymoclock.ui.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.gym.o.gymoclock.databinding.FragmentCalendarBinding
import com.gym.o.gymoclock.functionality.calendar_pr.onDateChangeListener
import com.gym.o.gymoclock.functionality.calendar_pr.showWorkoutDetails
import com.gym.o.gymoclock.functionality.calendar_pr.tableNameExists
import com.gym.o.gymoclock.utils.DateTimeUtils


class CalendarFragment : DialogFragment() {

    private var _binding: FragmentCalendarBinding? = null
    val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val calendarViewModel =
            ViewModelProvider(this)[CalendarViewModel::class.java]

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Log.i("CalendarFragment", "onCreateView")
        calendarViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it
        }

        onOpeningViewGetDateWorkout()
        onDateChangeListener()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onOpeningViewGetDateWorkout(){
        val date = DateTimeUtils.getDate()//String.format("%02d-%02d-%02d", dayOfMonth, month, year)
        DateTimeUtils.getCurrentMonth()
        DateTimeUtils.getCurrentYear()
        val tableName = "${DateTimeUtils.getCurrentMonth()}_${DateTimeUtils.getCurrentYear()}"//"${Month.of(month)}_$year"
        Log.d("TAG", "onDateSet: mm/dd/yyy: $date ${DateTimeUtils.setCalendarTableName()} $tableName")

        binding.buttonPopLayout.removeAllViews()
        if (tableNameExists(tableName))
            showWorkoutDetails(tableName, date)
    }
}