package com.gym.o.gymoclock.ui.calendar

import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gym.o.gymoclock.databinding.FragmentCalendarBinding
import com.gym.o.gymoclock.utils.DateTimeUtils


class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val mDisplayDate: TextView? = null
    private var mDateSetListener: OnDateSetListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val galleryViewModel =
            ViewModelProvider(this)[CalendarViewModel::class.java]

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textCalendar
        galleryViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it


            binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                val month = month + 1
                Log.d("TAG", "onDateSet: mm/dd/yyy: $dayOfMonth/$month/$year ${DateTimeUtils.setCalendarTableName()}")
                DateTimeUtils.setCalendarTableName()


                val date = "$month/$dayOfMonth/$year"



            }

//            mDateSetListener = OnDateSetListener { datePicker, year, month, day ->
//                    var month = month
//                    month = month + 1
//                    Log.d("TAG", "onDateSet: mm/dd/yyy: $month/$day/$year")
//                    val date = "$month/$day/$year"
//                    mDisplayDate?.text = date
//                }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}