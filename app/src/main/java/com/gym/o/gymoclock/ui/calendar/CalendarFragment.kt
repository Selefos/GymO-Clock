package com.gym.o.gymoclock.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.gym.o.gymoclock.databinding.FragmentCalendarBinding
import com.gym.o.gymoclock.functionality.calendar_pr.onDateChangeListener


class CalendarFragment : DialogFragment() {

    private var _binding: FragmentCalendarBinding? = null
    val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val calendarViewModel =
            ViewModelProvider(this)[CalendarViewModel::class.java]

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

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

}