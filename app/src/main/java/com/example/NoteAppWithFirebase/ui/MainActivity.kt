package com.example.NoteAppWithFirebase.ui

import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.NoteAppWithFirebase.adapter.NoteAdapter
import com.example.NoteAppWithFirebase.configs.Util
import com.example.NoteAppWithFirebase.databinding.ActivityMainBinding
import com.example.NoteAppWithFirebase.models.Note
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.addNoteBtn.setOnClickListener {
            startActivity(Intent(this, NoteDetailsActivity::class.java))
        }

        binding.menuBtn.setOnClickListener { showMenu() }

        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        val query = Util.getCollectionReferenceForCities()
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(query, Note::class.java).build()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        noteAdapter = NoteAdapter(options, this)
        binding.recyclerView.adapter = noteAdapter

    }

    private fun showMenu() {
        val popupMenu = PopupMenu(this,binding.menuBtn)
        popupMenu.menu.add("Logout")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {
            if (it.title == "Logout") {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
            true
        }
    }

    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        noteAdapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        noteAdapter.notifyDataSetChanged()
    }

}