package com.umutduran.mynotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umutduran.mynotes.databinding.ItemNoteBinding

class NoteAdapter(var noteList: ArrayList<NoteModel>,private val noteClickListener: EditNoteClickListener) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    val lastNoteList = noteList

    class NoteViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.binding.tvNoteCategory.text = "Category: ${lastNoteList[position].noteCategory}"
        holder.binding.tvNoteTitle.text = lastNoteList[position].noteTitle
        holder.binding.tvNoteContent.text = "Content: ${lastNoteList[position].noteContent}"

        when (lastNoteList[position].noteCategory) {
            "Personal" -> holder.binding.cvRecommend.setCardBackgroundColor(
                holder.itemView.context.resources.getColor(
                    android.R.color.holo_orange_light
                )
            )
            "Family" -> holder.binding.cvRecommend.setCardBackgroundColor(
                holder.itemView.context.resources.getColor(
                    android.R.color.holo_green_light
                )
            )
            "Work" -> holder.binding.cvRecommend.setCardBackgroundColor(
                holder.itemView.context.resources.getColor(
                    android.R.color.holo_red_light
                )
            )
            "Daily" -> holder.binding.cvRecommend.setCardBackgroundColor(
                holder.itemView.context.resources.getColor(
                    android.R.color.holo_blue_light
                )
            )
        }

        holder.binding.cvRecommend.setOnClickListener {
            if (holder.binding.tvNoteContent.visibility == View.GONE) holder.binding.tvNoteContent.visibility = View.VISIBLE
            else if (holder.binding.tvNoteContent.visibility == View.VISIBLE) holder.binding.tvNoteContent.visibility = View.GONE
        }

        holder.binding.ivEdit.setOnClickListener {
            noteClickListener.noteClicked(lastNoteList[position])
        }
    }

    override fun getItemCount() = lastNoteList.size

    fun updateNoteList(newList: ArrayList<NoteModel>) {
        lastNoteList.clear()
        lastNoteList.addAll(newList)
        notifyDataSetChanged()
    }

}