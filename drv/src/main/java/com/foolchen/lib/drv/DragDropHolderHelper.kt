package com.foolchen.lib.drv

/**
 * 给RecyclerView.ViewHolder实现用的辅助接口
 *
 * @author chenchong
 * 2018/12/10
 * 5:04 PM
 */
interface DragDropHolderHelper {

  /**
   * 用于标识当前ViewHolder是否可以进行拖动
   */
  fun isDraggable(): Boolean

  /**
   * 拖动排序模式开启
   */
  fun onDraggingModeStart() {}

  /**
   * 拖动排序模式关闭
   */
  fun onDraggingModeStop() {}
}