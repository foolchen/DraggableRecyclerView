package com.foolchen.lib.samples

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foolchen.lib.drv.DragDropCallBack
import com.foolchen.lib.drv.DragDropHelper
import com.foolchen.lib.drv.swap
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_item_name_view_holder.view.*

class MainActivity : AppCompatActivity(), DragDropCallBack {
  private val mLayoutManager: GridLayoutManager by lazy { GridLayoutManager(this, 1) }
  private val mNames: MutableList<String> = ArrayList()
  private val mAdapter: NamesAdapter = NamesAdapter()
  private val mHelper: DragDropHelper = DragDropHelper(this)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    for (position in 0 until 100) {
      mNames.add("Name $position")
    }

    mLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
      override fun getSpanSize(position: Int): Int = 1
    }
    mRecyclerView.layoutManager = mLayoutManager
    mRecyclerView.adapter = mAdapter

    mHelper.attachToRecyclerView(mRecyclerView)
  }

  inner class NamesAdapter : RecyclerView.Adapter<NameViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): NameViewHolder {
      return NameViewHolder(
          LayoutInflater.from(parent.context).inflate(R.layout.layout_item_name_view_holder, parent,
              false))
    }

    override fun getItemCount(): Int = mNames.size

    override fun onBindViewHolder(holder: NameViewHolder, position: Int) {
      holder.itemView.mTextName.text = mNames[position]
    }
  }

  inner class NameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

  override fun onMove(rv: RecyclerView, from: Int, to: Int): Boolean {
    mNames.swap(from, to)
    return false
  }
}
