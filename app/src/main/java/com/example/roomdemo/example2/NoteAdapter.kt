package com.example.roomdemo.example2

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Update
import com.example.roomdemo.R

class NoteAdapter(val context: Context,
                  val noteClickDeleteInterface: NoteClickDeleteInterface,
                  val noteClickInterface: NoteClickInterface)  :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>(){
    private val allNotes = ArrayList<Note>()

    // on below line we are creating a view holder class.
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are creating an initializing all our
        // variables which we have added in layout file.
        val noteTV = itemView.findViewById<TextView>(R.id.tvNote)
        val dateTV = itemView.findViewById<TextView>(R.id.tvDate)
        val deleteIV = itemView.findViewById<ImageView>(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.note_rv_item,
            parent, false
        )
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {

return allNotes.size   }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.noteTV.setText(allNotes.get(position).noteTitle)
        holder.dateTV.setText("Last Updated : " + allNotes.get(position).timeStamp)
        holder.deleteIV.setOnClickListener {
            noteClickDeleteInterface.onDeleteIconClick(allNotes.get(position))
        }

        holder.itemView.setOnClickListener {
            noteClickInterface.onNoteClick(allNotes.get(position))
        }
    }
fun updateList(newList: List<Note>){
    allNotes.clear()
    allNotes.addAll(newList)
    notifyDataSetChanged()
}
}

interface NoteClickDeleteInterface {

    fun onDeleteIconClick(note: Note)
}

interface NoteClickInterface {

    fun onNoteClick(note: Note)
}