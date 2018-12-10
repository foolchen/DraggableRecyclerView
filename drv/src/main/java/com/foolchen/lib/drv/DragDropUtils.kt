package com.foolchen.lib.drv

/**
 * 交换一个[MutableList]中的两个元素
 */
fun <T> MutableList<T>.swap(from: Int, to: Int): Boolean {
  if (from == to) return false
  if (from < to) {
    val fromValue = removeAt(from)
    add(to + 1, fromValue)
  } else {
    add(to, removeAt(from))
  }
  return true
}