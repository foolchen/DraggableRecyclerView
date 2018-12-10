package com.foolchen.lib.drv

import android.support.v7.widget.RecyclerView

/**
 * @author chenchong
 * 2018/12/10
 * 4:34 PM
 */
interface DragDropCallBack {
    /**
     * 移动数据
     * @return true-拦截刷新操作，调用者手动处理；false-调用者不处理
     */
    fun onMove(rv: RecyclerView, from: Int, to: Int): Boolean
}