package com.chanho.loveday.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.chanho.loveday.MemoFragment
import com.chanho.loveday.R
import com.chanho.loveday.model.MemoModel
import java.util.HashMap

class MemoItemAdapter(val fragment: Fragment, val memoList: List<MemoModel>?): RecyclerView.Adapter<MemoItemAdapter.GridAdapter>() {

    class GridAdapter(val layout: View): RecyclerView.ViewHolder(layout) {
        var title = layout.findViewById<TextView>(R.id.momoGridTitleText)
        var content = layout.findViewById<TextView>(R.id.memoGridContentText)
        var editBtn = layout.findViewById<ImageButton>(R.id.memoEditButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridAdapter {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.memo_grid_item, parent, false)

        return GridAdapter(view)
    }

    override fun onBindViewHolder(holder: GridAdapter, position: Int) {
        holder.title.text = memoList?.get(position)?.title
        holder.content.text = memoList?.get(position)?.content

        holder.editBtn.setOnClickListener {
            var id = memoList?.get(position)?.id ?: ""
            var writer = memoList?.get(position)?.writer?.writer ?: ""
            var title = memoList?.get(position)?.title ?: "title"
            var content = memoList?.get(position)?.content ?: "content"

            if (fragment is MemoFragment) {
                val param = HashMap<String, Any>()
                param["writer"] = writer
                param["id"] = id
                param["title"] = title
                param["content"] = content
                fragment.moreAction(param)
            }
        }

    }

    override fun getItemCount(): Int {
        return memoList?.size ?: 0
    }
}