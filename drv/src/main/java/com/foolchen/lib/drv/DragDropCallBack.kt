package com.foolchen.lib.drv

import android.support.v7.widget.RecyclerView

/**
 * 拖动排序的回调接口
 *
 * @author chenchong
 * 2018/12/10
 * 4:34 PM
 */
interface DragDropCallBack {
  /**
   * 交换两个位置的数据
   *
   * @return true-拦截刷新操作，调用者手动处理；false-调用者不处理
   */
  fun onSwap(rv: RecyclerView, from: Int, to: Int): Boolean

  /**
   * 在vh处开始了拖动排序模式
   */
  fun onDragStart(vh: RecyclerView.ViewHolder) {}

  /**
   * 在vh处结束了拖动排序模式
   */
  fun onDragStop(vh: RecyclerView.ViewHolder) {}
}