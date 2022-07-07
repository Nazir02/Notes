package com.example.notes.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.model.NoteModel
import com.example.notes.view.MainFragment
import kotlinx.android.synthetic.main.item.view.*

class MainAdapter : RecyclerView.Adapter<MainAdapter.MyViewHolder>() {
    private var listNote = emptyList<NoteModel>()


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textViewNotes: TextView? = null
        private var subtextViewNotes: TextView? = null


        init {
            textViewNotes = itemView.findViewById(R.id.titleTextView)
            subtextViewNotes = itemView.findViewById(R.id.subtextView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.titleTextView.text = listNote[position].title
        holder.itemView.subtextView.text = listNote[position].subTitle

    }

    override fun getItemCount(): Int {
        return listNote.size
    }

    fun setList(list: List<NoteModel>) {
        listNote = list
        notifyDataSetChanged()
    }

    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.setOnClickListener {
            MainFragment.clikTitle(listNote[holder.adapterPosition])
        }
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        holder.itemView.setOnClickListener(null)
    }


}


