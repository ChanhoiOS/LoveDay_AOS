package com.chanho.loveday.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.chanho.loveday.R
import com.chanho.loveday.model.MemoModel

class MemoItemAdapter(val memoList: List<MemoModel>?): RecyclerView.Adapter<MemoItemAdapter.GridAdapter>() {

    class GridAdapter(val layout: View): RecyclerView.ViewHolder(layout) {
        var title = layout.findViewById<TextView>(R.id.momoGridTitleText)
        var content = layout.findViewById<TextView>(R.id.memoGridContentText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridAdapter {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.memo_grid_item, parent, false)

        return GridAdapter(view)
    }

    override fun onBindViewHolder(holder: GridAdapter, position: Int) {
       holder.title.text = memoList?.get(position)?.title
        holder.content.text = memoList?.get(position)?.content
    }

    override fun getItemCount(): Int {
        return memoList?.size ?: 0
    }
}