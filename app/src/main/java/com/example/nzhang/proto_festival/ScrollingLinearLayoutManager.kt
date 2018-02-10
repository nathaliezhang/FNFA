package com.example.nzhang.proto_festival

/**
 * Created by mel on 05/02/2018.
 */
import android.content.Context
import android.graphics.PointF
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView

class ScrollingLinearLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean, private val duration: Int) : LinearLayoutManager(context, orientation, reverseLayout) {

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?,
                                        position: Int) {
        val firstVisibleChild = recyclerView.getChildAt(0)
        val itemHeight = firstVisibleChild.height
        val currentPosition = recyclerView.getChildLayoutPosition(firstVisibleChild)

        var distanceInPixels = Math.abs((currentPosition - (position)) * itemHeight)
        if (distanceInPixels == 0) {
            distanceInPixels = Math.abs(firstVisibleChild.y).toInt()
        }
        val smoothScroller = SmoothScroller(recyclerView.context, distanceInPixels, duration)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    private inner class SmoothScroller(context: Context, distanceInPixels: Int, duration: Int) : LinearSmoothScroller(context) {
        private val distanceInPixels: Float
        private val duration: Float
        private val TARGET_SEEK_SCROLL_DISTANCE_PX: Int = 1000000

        init {
            this.distanceInPixels = distanceInPixels.toFloat()
            val millisPerPx = calculateSpeedPerPixel(context.resources.displayMetrics)
            this.duration = (if (distanceInPixels < TARGET_SEEK_SCROLL_DISTANCE_PX) (Math.abs(distanceInPixels) * millisPerPx).toInt() else duration).toFloat()
        }

        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
            return this@ScrollingLinearLayoutManager.computeScrollVectorForPosition(targetPosition)
        }

        override fun calculateTimeForScrolling(dx: Int): Int {
            val proportion = dx.toFloat() / distanceInPixels
            return (duration * proportion).toInt()
        }
    }
}
