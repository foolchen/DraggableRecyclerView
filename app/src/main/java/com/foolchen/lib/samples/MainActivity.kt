package com.foolchen.lib.samples

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Toast
import com.foolchen.lib.drv.DragDropCallBack
import com.foolchen.lib.drv.DragDropHelper
import com.foolchen.lib.drv.DragDropHolderHelper
import com.foolchen.lib.drv.IShadow
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_item_name_view_holder.view.*

class MainActivity : AppCompatActivity(), DragDropCallBack {
  private val mLayoutManager: GridLayoutManager by lazy { GridLayoutManager(this, 1) }
  private val mNames: MutableList<String> = ArrayList()
  private val mAdapter: NamesAdapter = NamesAdapter()
  private val mHelper: DragDropHelper = DragDropHelper(this)
  private var isGridMode = false
  private var mShadowDrawable: Drawable? = null

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
    mShadowDrawable = ContextCompat.getDrawable(this, R.drawable.drawable_stroke)
  }

  override fun onDestroy() {
    mHelper.detachFromRecyclerView(mRecyclerView)
    super.onDestroy()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_main_activity, menu)
    return true
  }

  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    menu.findItem(R.id.menu_drag_mode)?.isChecked = mHelper.isInDragMode()
    menu.findItem(R.id.menu_grid_mode)?.isChecked = isGridMode
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val itemId = item.itemId
    when (itemId) {
      R.id.menu_drag_mode ->
        if (mHelper.isInDragMode()) {
          mHelper.stopDragMode()
        } else {
          mHelper.startDragMode()
        }
      R.id.menu_grid_mode -> {
        if (isGridMode) {
          mLayoutManager.spanCount = 1
          isGridMode = false
        } else {
          mLayoutManager.spanCount = 3
          isGridMode = true
        }
        mAdapter.notifyDataSetChanged()
      }
    }
    return true
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
      holder.onDraggingModeStart()
    }
  }

  inner class NameViewHolder(itemView: View) : RecyclerView.ViewHolder(
      itemView), DragDropHolderHelper, IShadow {
    private val mRect = Rect()

    init {
      mHelper.attachToViewHolder(this)
      itemView.setOnClickListener {
        Toast.makeText(this@MainActivity, itemView.mTextName.text, Toast.LENGTH_SHORT).show()
      }
    }

    fun onBindView() {
      if (adapterPosition == 0) {
        itemView.setBackgroundColor(Color.GRAY)
      } else {
        when {
          mHelper.isDragging(adapterPosition) -> {
          }
          mHelper.isInDragMode() -> itemView.setBackgroundColor(Color.YELLOW)
          else -> itemView.background = null
        }
      }
    }

    override fun isDraggable(): Boolean {
      return adapterPosition != 0
    }

    override fun onDraggingModeStart() {
      onBindView()
    }

    override fun onDraggingModeStop() {
      onBindView()
    }

    override fun getShadow(): Drawable? {
      mRect.set(itemView.left + 10, itemView.top + 10, itemView.right - 10, itemView.bottom - 10)
      mShadowDrawable?.bounds = mRect
      return mShadowDrawable
    }
  }

  override fun onSwap(rv: RecyclerView, from: Int, to: Int): Boolean {
    val fromValue = mNames.removeAt(from)
    mNames.add(to, fromValue)
    return false
  }

  override fun onDragStart(vh: RecyclerView.ViewHolder) {
    vh.itemView.background = ContextCompat.getDrawable(this, R.drawable.drawable_item_background)
  }

  override fun onDragStop(vh: RecyclerView.ViewHolder) {
    if (mHelper.isInDragMode()) {
      vh.itemView.setBackgroundColor(Color.YELLOW)
    } else {
      vh.itemView.background = null
    }
  }
}
