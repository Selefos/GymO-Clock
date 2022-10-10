package com.gym.o.gymoclock.functionality.calendar_pr

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseScreening
import com.gym.o.gymoclock.utils.FormatUtils
import io.paperdb.Paper

class ExerciseScreeningAdapter(private val mContext: Context, private val mResource: Int,
    objects: ArrayList<ExerciseScreening>, private val bookName: String, private val keyName: String) : ArrayAdapter<ExerciseScreening?>(
    mContext, mResource, objects as List<ExerciseScreening?>
) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(mResource, parent, false)
        readDataList(view, position)

        return view!!
    }

    private fun readDataList(rowView: View, position: Int) {
        val exerciseNameScreen = rowView.findViewById<TextView>(R.id.exercise_name_screen)
        val table: TableLayout = rowView.findViewById(R.id.show_details_table)
        table.gravity = Gravity.START
        val resources = context.resources

        val textViewParams: TableRow.LayoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
        textViewParams.setMargins(0, 0, 40, 0)

        val tableRowParams = TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT)
        tableRowParams.setMargins(0, 0, 0, 42)

        val data = Paper.book(keyName).read<HashMap<String, ArrayList<ArrayList<String>>>>(bookName)
        val textViewTextAlignment = View.TEXT_ALIGNMENT_CENTER

        exerciseNameScreen.text = getItem(position)!!.exerciseNameValue

        for (key in data!!.keys) {

            val rowDetails = TableRow(context)
            rowDetails.gravity = Gravity.START
            rowDetails.layoutParams = tableRowParams

            val roundsDetails = TextView(context)
            roundsDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
            roundsDetails.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
            roundsDetails.textAlignment = textViewTextAlignment
            roundsDetails.text = key
            roundsDetails.layoutParams = textViewParams
            rowDetails.addView(roundsDetails)

            // Round Weight
            val roundWeight = TextView(context)
            roundWeight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
            roundWeight.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
            roundWeight.textAlignment = textViewTextAlignment

            val weightString = "${data[key]!![1][0]}: "
            val weightValue = data[key]!![1][getItem(position)!!.index + 1]

            roundWeight.text = FormatUtils.spannableText(weightString, weightValue, resources)
            roundWeight.layoutParams = textViewParams
            rowDetails.addView(roundWeight)

            // Round Reps
            val roundReps = TextView(context)
            roundReps.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
            roundReps.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
            roundReps.textAlignment = textViewTextAlignment

            val repsString = "${data[key]!![2][0]}: "
            val repsValue = data[key]!![2][getItem(position)!!.index + 1]

            roundReps.text = FormatUtils.spannableText(repsString, repsValue, resources)
            roundWeight.layoutParams = textViewParams
            rowDetails.addView(roundReps)

            table.addView(rowDetails)
        }

    }

}

