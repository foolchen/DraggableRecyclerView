package com.foolchen.lib.drv

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MotionEvent
import android.view.View

class DragDropHelper(callback: DragDropCallBack) {
  private val mCallBack = object : DragDropCallBack {
    override fun onSwap(rv: RecyclerView, from: Int, to: Int): Boolean {
      return callback.onSwap(rv, from, to)
    }

    override fun onDragStart(vh: RecyclerView.ViewHolder) {
      callback.onDragStart(vh)
      mDraggingPosition = vh.adapterPosition
      if (!isInDragMode()) {
        mInDraggingMode = true
        onDraggingModeStart()
      }
    }

    override fun onDragStop(vh: RecyclerView.ViewHolder) {
      callback.onDragStop(vh)
      mDraggingPosition = -1
    }
  }
  private val mItemTouchHelperCallBack = InternalDragDropCallBack(mCallBack)
  private val mItemTouchHelper: ItemTouchHelper = ItemTouchHelper(
      mItemTouchHelperCallBack)
  private var mInDraggingMode = false
  private var mDraggingPosition = -1
  private var mRecyclerView: RecyclerView? = null

  /**
   * 手动开启拖动排序模式
   */
  fun startDragMode() {
    if (!mInDraggingMode) {
      mInDraggingMode = true
      onDraggingModeStart()
    }
  }

  fun stopDragMode() {
    if (mInDraggingMode) {
      mInDraggingMode = false
      onDraggingModeStop()
    }
  }

  fun setShadow(shadow: Drawable? = null) {
    mItemTouchHelperCallBack.shadow = shadow
  }

  fun isInDragMode(): Boolean = mInDraggingMode

  fun isDragging(position: Int): Boolean {
    return position == mDraggingPosition
  }

  fun attachToRecyclerView(recyclerView: RecyclerView?) {
    mItemTouchHelper.attachToRecyclerView(recyclerView)
    mRecyclerView = recyclerView
  }

  fun detachFromRecyclerView(@Suppress("UNUSED_PARAMETER") recyclerView: RecyclerView?) {
    mRecyclerView = null
  }

  fun attachToViewHolder(holder: RecyclerView.ViewHolder) {
    var onTouchListener = holder.itemView.getTag(
        R.id.drv_on_touch_listener) as? View.OnTouchListener
    if (onTouchListener == null) {
      onTouchListener = View.OnTouchListener { _, event ->
        // 在移动手指时，才触发拖动
        // 防止点击事件被屏蔽
        if (event.actionMasked == MotionEvent.ACTION_MOVE && isInDragMode()) {
          mItemTouchHelper.startDrag(holder)
          return@OnTouchListener true
        }
        return@OnTouchListener false
      }
    }
    holder.itemView.setOnTouchListener(onTouchListener)
    holder.itemView.setTag(R.id.drv_on_touch_listener, onTouchListener)
  }

  private fun onDraggingModeStart() {
    onDraggingModeChanged(true)
  }

  private fun onDraggingModeStop() {
    onDraggingModeChanged(false)
  }

  private fun onDraggingModeChanged(isInDraggingMode: Boolean) {
    mRecyclerView?.let { rv ->
      val childCount = rv.childCount
      for (position in 0 until childCount) {
        rv.getChildAt(position)?.let { child ->
          if (isInDraggingMode) {
            (rv.findContainingViewHolder(child) as? DragDropHolderHelper)?.onDraggingModeStart()
          } else {
            (rv.findContainingViewHolder(child) as? DragDropHolderHelper)?.onDraggingModeStop()
          }
        }
      }
    }
  }
}

private class InternalDragDropCallBack(
    val callback: DragDropCallBack, var shadow: Drawable? = null) : ItemTouchHelper.Callback() {
  // Should return a composite flag which defines the enabled swap directions in each state (idle, swiping, dragging).
  // 返回定义的标识，用于标识ViewHolder当前的状态
  override fun getMovementFlags(rv: RecyclerView, holder: RecyclerView.ViewHolder): Int {
    return if ((holder as? DragDropHolderHelper)?.isDraggable() != false) {
      val dragFlags: Int = if (rv.layoutManager is GridLayoutManager) {
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
      } else {
        ItemTouchHelper.UP or ItemTouchHelper.DOWN
      }
      makeMovementFlags(dragFlags, 0) // swipeFlags = 0，表示不处理滑动事件
    } else {
      makeMovementFlags(0, 0)
    }
  }

  // Called when ItemTouchHelper wants to swap the dragged item from its old position to the new position.
  // 当ItemTouchHelper想要交换两个条目的顺序时调用该方法
  override fun onMove(
      rv: RecyclerView, from: RecyclerView.ViewHolder,
      to: RecyclerView.ViewHolder
  ): Boolean {
    if ((to as? DragDropHolderHelper)?.isDraggable() != false) {
      val fromPosition = from.adapterPosition
      val toPosition = to.adapterPosition
      if (fromPosition == RecyclerView.NO_POSITION || toPosition == RecyclerView.NO_POSITION) return false
      if (fromPosition == toPosition) return false
      if (!callback.onSwap(rv, fromPosition, toPosition)) {
        rv.adapter?.notifyItemMoved(fromPosition, toPosition)
      }
      return true
    } else {
      return false
    }
  }

  // Called when a ViewHolder is swiped by the user.
  // 当用于滑动了ViewHolder时调用该方法
  override fun onSwiped(rv: RecyclerView.ViewHolder, position: Int) {
    // 不支持滑动
  }

  override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
      callback.onDragStart(viewHolder)
    }
    super.onSelectedChanged(viewHolder, actionState)
  }

  override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
    super.clearView(recyclerView, viewHolder)
    callback.onDragStop(viewHolder)
  }

  override fun isLongPressDragEnabled(): Boolean {
    return true
  }

  override fun isItemViewSwipeEnabled(): Boolean {
    return false
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
    (viewHolder as? IShadow)?.getShadow()?.draw(c)

    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
  }

}
