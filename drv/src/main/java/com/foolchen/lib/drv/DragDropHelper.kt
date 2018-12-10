package com.foolchen.lib.drv

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log

class DragDropHelper(callback: DragDropCallBack) : ItemTouchHelper(
    InternalDragDropCallBack(callback))

private class InternalDragDropCallBack(
    val callback: DragDropCallBack) : ItemTouchHelper.Callback() {
  // Should return a composite flag which defines the enabled swap directions in each state (idle, swiping, dragging).
  // 返回定义的标识，用于标识ViewHolder当前的状态
  override fun getMovementFlags(rv: RecyclerView, holder: RecyclerView.ViewHolder): Int {
    val dragFlags: Int = if (rv.layoutManager is GridLayoutManager) {
      ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    } else {
      ItemTouchHelper.UP or ItemTouchHelper.DOWN
    }
    return makeMovementFlags(dragFlags, 0) // swipeFlags = 0，表示不处理滑动事件
  }

  // Called when ItemTouchHelper wants to swap the dragged item from its old position to the new position.
  // 当ItemTouchHelper想要交换两个条目的顺序时调用该方法
  override fun onMove(
      rv: RecyclerView, from: RecyclerView.ViewHolder,
      to: RecyclerView.ViewHolder
  ): Boolean {
    Log.d("MainActivity", "onMove:(from = ${from.adapterPosition} , to = ${to.adapterPosition})")
    val fromPosition = from.adapterPosition
    val toPosition = to.adapterPosition
    if (fromPosition == RecyclerView.NO_POSITION || toPosition == RecyclerView.NO_POSITION) return false
    if (fromPosition == toPosition) return false
    if (!callback.onMove(rv, fromPosition, toPosition)) {
      rv.adapter?.notifyItemMoved(fromPosition, toPosition)
    }
    return true
  }

  // Called when a ViewHolder is swiped by the user.
  // 当用于滑动了ViewHolder时调用该方法
  override fun onSwiped(rv: RecyclerView.ViewHolder, position: Int) {
    Log.d("MainActivity", "onSwiped(position = $position")
  }
}