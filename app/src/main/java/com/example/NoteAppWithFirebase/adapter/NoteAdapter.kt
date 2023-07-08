package com.example.NoteAppWithFirebase.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.NoteAppWithFirebase.R
import com.example.NoteAppWithFirebase.configs.Util
import com.example.NoteAppWithFirebase.models.Note
import com.example.NoteAppWithFirebase.ui.NoteDetailsActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference

class NoteAdapter(options: FirestoreRecyclerOptions<Note>, context: Context) :
    FirestoreRecyclerAdapter<Note, NoteAdapter.CityViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_city_item,parent,false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int, note: Note) {
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content
        holder.timestampTextView.text = Util.timestampToString(note.timestamp)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, NoteDetailsActivity::class.java)
            intent.putExtra("title", note.title)
            intent.putExtra("content", note.content)
            val docId = this.snapshots.getSnapshot(position).id
            intent.putExtra("docId", docId)
            it.context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            val deleteDocId = this.snapshots.getSnapshot(position).id
            alertDialogSet(it, deleteDocId)
            true
        }

    }

    class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.city_title_text_view)
        val contentTextView: TextView = itemView.findViewById(R.id.city_content_text_view)
        val timestampTextView: TextView = itemView.findViewById(R.id.city_timestamp_text_view)
    }

    private fun alertDialogSet(view: View, deleteDocId: String) {
        val alert = AlertDialog.Builder(view.context)
        alert.setTitle("Are you sure?")
        alert.setMessage("This action will delete the note.")
        alert.setCancelable(false)

        alert.setPositiveButton("Yes") { dialogInterface, i ->
            deleteNoteFromFirebase(view, deleteDocId)
        }

        alert.setNegativeButton("No") { dialogInterface, i ->

        }
        alert.show()
    }

    private fun deleteNoteFromFirebase(view: View, deleteDocId: String) {
        val documentReference : DocumentReference =
            Util.getCollectionReferenceForCities().document(deleteDocId)

        documentReference.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                    Util.showToast(view.context, "Note deleted successfully")
            } else {
                    Util.showToast(view.context, "Failed while deleting note")
            }
        }

    }

}