package com.gym.o.gymoclock.functionality.workout_pr.recycler_items_swipe

import android.content.Context
import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gym.o.gymoclock.ui.workout.WorkoutFragment

private fun dipToPx(dipValue: Float, context: Context): Int {
    return (dipValue * context.resources.displayMetrics.density).toInt()
}

fun WorkoutFragment.setItemTouchHelper() {
    ItemTouchHelper(object : ItemTouchHelper.Callback() {

        //limit of swipe
        private val limitScroll = dipToPx(40f, requireActivity())
        private var currentScrollX = 0
        private var currentScrollXWhenActive = 0
        private var initXWhenInActive = 0f
        private var firstInActive = false

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val drag = 0
            val swipe = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            return makeMovementFlags(drag, swipe)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }

        override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float {
            return Integer.MAX_VALUE.toFloat()
        }

        override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
            return Integer.MAX_VALUE.toFloat()
        }

        override fun onChildDraw(
            c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                if (dX == 0f) {
                    currentScrollX = viewHolder.itemView.scrollX
                    firstInActive = true
                }

                if (isCurrentlyActive) {
                    var scrollOffSet = currentScrollX + (-dX).toInt()

                    if (scrollOffSet > limitScroll)
                        scrollOffSet = limitScroll
                    else if (scrollOffSet < 0)
                        scrollOffSet = 0
                    viewHolder.itemView.scrollTo(scrollOffSet, 0)
                } else {

                    if (firstInActive) {
                        firstInActive = false
                        currentScrollXWhenActive = viewHolder.itemView.scrollX
                        initXWhenInActive = dX
                    }

                    if (viewHolder.itemView.scrollX < limitScroll) {
                        viewHolder.itemView.scrollTo((currentScrollXWhenActive * dX / initXWhenInActive).toInt(), 0)
                    }
                }
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)

            if (viewHolder.itemView.scrollX > limitScroll)
                viewHolder.itemView.scrollTo(limitScroll, 0)
            else if (viewHolder.itemView.scrollX < 0)
                viewHolder.itemView.scrollTo(0, 0)
        }
    }).apply { attachToRecyclerView(recyclerView) }
}

//const val swipeDirections = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
//private val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, swipeDirections) {
//    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
//        return false
//    }
//
//    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//        val position = viewHolder.adapterPosition
//
//        when (direction) {
//            ItemTouchHelper.LEFT  -> {
//                dataList.removeAt(position)
//                listAdapter.notifyItemRemoved(position)
//            }
//            ItemTouchHelper.RIGHT -> {}
//        }
//    }
//}