package com.szr.co.smart.qr.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemGridDecoration : RecyclerView.ItemDecoration {

    val spanCount: Int
    val spacing: Int
    val lastBottom: Int
    val includeEdge: Boolean
    val bottom: Int
    val top: Int

    constructor(spanCount: Int, spacing: Int, lastBottom: Int, includeEdge: Boolean) {
        this.spanCount = spanCount
        this.spacing = spacing
        this.lastBottom = lastBottom
        this.includeEdge = includeEdge
        this.top = spacing
        this.bottom = spacing
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val itemCount = parent.adapter!!.itemCount
//        if (position <= 1)
//            return;
        //        if (position <= 1)
//            return;
        val column = position % spanCount // item column

        if (includeEdge) {
            outRect.left =
                spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right =
                (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)


            if (position < spanCount) { // top edge
                outRect.top = 0
            }
            if (position > (itemCount - spanCount)) {
                outRect.bottom = lastBottom
            } else {
                outRect.bottom = bottom // item bottom
            }
        } else {
            outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
            outRect.right =
                spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }
}